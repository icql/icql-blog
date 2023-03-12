package *;

import *

@Slf4j
@Service
public class SpdProductBizService {

    //region 依赖注入

    @Autowired
    private GeoCacheUtils geoCacheUtils;

    @Autowired
    private ProductCacheComponent productCacheComponent;

    @Autowired
    private EsNewSearchEngineClientFacade esNewSearchEngineClientFacade;

    @Autowired
    private PromoteFacade promoteFacade;

    @Autowired
    private ActivityProductFacade activityProductFacade;

    @Autowired
    private RestfulProductInventory productInventory;

    @Autowired
    private RestfulProductPrice restfulProductPrice;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Qualifier("executor")
    @Autowired
    private ThreadPoolTaskExecutor executor;

    //endregion

    //region 常量

    private static final int INT_ZERO = 0;

    private static final int INT_ONE = 1;

    private static final int SPD_PRODUCT_MAX_NUM = 5;

    private static final int ES_SEARCH_PRODUCT_MAX_NUM = 2000;

    private static final String SPD = "SPD";

    private static final String SPD_PRODUCT_SORT_TYPE_SALES_COUNT_DESC = "SALES_COUNT_DESC";

    private static final String SPD_PRODUCT_SORT_TYPE_PRICE_ASC = "PRICE_ASC";

    private static final String SPD_PRODUCT_SORT_TYPE_DISTANCE_ASC = "DISTANCE_ASC";

    private static final String SPD_PRODUCT_SORT_TYPE_DEFAULT = "DEFAULT";

    private static final String SPD_PRODUCT_LOG_PREFIX = "根据spd数据搜索商品,";

    //endregion


    public List<SearchEngineSpdProductResult> listByParam(SearchEngineSpdProductParam param) {
        long start = System.currentTimeMillis();

        String userLoginId = param.getUserLoginId();
        String companyId = param.getCompanyId();
        String userGeoId = param.getUserGeoId();
        String sortType = param.getSortType();
        List<SearchEngineSpdProductParam.SpdParam> spdParams = param.getSpdParams();
        int spdParamsSize = spdParams.size();

        List<SearchEngineSpdProductResult> results = Lists.newArrayListWithCapacity(spdParamsSize);

        //处理返回值：根据入参组装
        List<String> brandIdPartsNos = Lists.newArrayListWithCapacity(spdParamsSize);
        assembleResultByParam(results, spdParams, brandIdPartsNos);

        //查询商城es：销售区域，品类授权，黑白名单，商家下线，上架，有库存，按照销量排序
        long start4Es = System.currentTimeMillis();
        List<SearchEngineProduct> searchResultProducts = searchMallEsProducts(
                userLoginId,
                companyId,
                userGeoId,
                brandIdPartsNos);
        log.info(SPD_PRODUCT_LOG_PREFIX + "查询es耗时={}", System.currentTimeMillis() - start4Es);

        if (CollectionUtils.isEmpty(searchResultProducts)) {
            return results;
        }

        //处理返回值：根据es结果组装
        assembleResultByMallEs(results, searchResultProducts);
        //处理返回值：根据销量排序，提前过滤有效商品id
        List<String> productIds = Lists.newArrayListWithCapacity(searchResultProducts.size());
        sortResultBySalesCount(results, sortType, productIds);

        //处理返回值：根据并行查询的商品信息
        long start4Product = System.currentTimeMillis();
        assembleResultByQueryProductInfoParallel(results, userLoginId, companyId, userGeoId, productIds);
        log.info(SPD_PRODUCT_LOG_PREFIX + "查询商品信息耗时={}", System.currentTimeMillis() - start4Product);

        //处理返回值：根据价格/距离/默认排序，每个SPD截取前5个商品
        sortResultBySortType(results, sortType);

        log.info(SPD_PRODUCT_LOG_PREFIX + "总耗时={}", System.currentTimeMillis() - start);
        return results;
    }


    //region 私有方法

    private List<SearchEngineProduct> searchMallEsProducts(String userLoginId,
                                                           String companyId,
                                                           String userGeoId,
                                                           List<String> brandIdPartsNos) {
        UserAddress userAddress = geoCacheUtils.getUserDetailAddressByGeoId(userGeoId, userLoginId, companyId);
        if (userAddress == null) {
            return Collections.emptyList();
        }
        List<String> availableStoreIds = productCacheComponent.listAvailableStoreIdsByParam(userAddress.getCountyGeoId(), userLoginId, companyId);
        if (CollectionUtils.isEmpty(availableStoreIds)) {
            return Collections.emptyList();
        }
        EsSearchEngineProductQueryParam searchParam = new EsSearchEngineProductQueryParam();
        searchParam.setStoreIn(availableStoreIds);
        searchParam.setGeoIds(Lists.newArrayList(
                userAddress.getCityGeoId(),
                userAddress.getProvinceGeoId(),
                UserAddress.CHN)
                .stream()
                .filter(Objects::nonNull).collect(Collectors.toList()));
        searchParam.setSalesScopeGeoIds(Lists.newArrayList(
                userAddress.getCountyGeoId(),
                userAddress.getCityGeoId(),
                userAddress.getProvinceGeoId(),
                UserAddress.CHN)
                .stream()
                .filter(Objects::nonNull).collect(Collectors.toList()));
        searchParam.setHasInventory(EsSearchEngineProductQueryParam.InventoryEnum.Y);
        searchParam.setBrandIdPartsNoList(brandIdPartsNos);
        searchParam.setManufacturerPartyId(SPD);
        searchParam.setSort(EsSearchEngineProductQueryParam.SortEnum.SALECOUNT_DESC);
        searchParam.setPageNum(INT_ONE);
        searchParam.setPageSize(ES_SEARCH_PRODUCT_MAX_NUM);
        PagedResult<EsSearchEngineProductResult> searchResult = esNewSearchEngineClientFacade.listPageSearchEngineProductByParam(searchParam);
        return Optional.ofNullable(searchResult.getData()).map(EsSearchEngineProductResult::getProducts).orElse(Collections.emptyList());
    }

    private void assembleResultByParam(List<SearchEngineSpdProductResult> results,
                                       List<SearchEngineSpdProductParam.SpdParam> spdParams,
                                       List<String> brandIdPartsNos) {
        for (SearchEngineSpdProductParam.SpdParam spdParam : spdParams) {
            brandIdPartsNos.add(String.join("@", spdParam.getBrandCode(), spdParam.getAeCode()));
            results.add(new SearchEngineSpdProductResult()
                    .setId(spdParam.getId())
                    .setAeCode(spdParam.getAeCode())
                    .setBrandCode(spdParam.getBrandCode())
                    .setProducts(Collections.emptyList()));
        }
    }

    private void assembleResultByMallEs(List<SearchEngineSpdProductResult> results,
                                        List<SearchEngineProduct> searchResultProducts) {
        Map<String, List<SearchEngineProduct>> searchProductsByBrandIdParsNo = Maps.newHashMapWithExpectedSize(searchResultProducts.size());
        for (SearchEngineProduct searchProduct : searchResultProducts) {
            String key = String.join("@",
                    StringBlankUtils.trimAll(StringUtils.upperCase(searchProduct.getProductBrandId())),
                    StringBlankUtils.trimAll(StringUtils.upperCase(searchProduct.getProductPartNum())));
            List<SearchEngineProduct> searchEngineProducts = searchProductsByBrandIdParsNo.get(key);
            if (CollectionUtils.isEmpty(searchEngineProducts)) {
                searchProductsByBrandIdParsNo.put(key, Lists.newArrayList(searchProduct));
            } else {
                searchEngineProducts.add(searchProduct);
            }
        }
        for (SearchEngineSpdProductResult result : results) {
            String id = result.getId();
            String brandCode = result.getBrandCode();
            String aeCode = result.getAeCode();
            String key = String.join("@",
                    StringBlankUtils.trimAll(StringUtils.upperCase(brandCode)),
                    StringBlankUtils.trimAll(StringUtils.upperCase(aeCode)));
            List<SearchEngineProduct> searchProducts = searchProductsByBrandIdParsNo.get(key);
            if (CollectionUtils.isNotEmpty(searchProducts)) {
                List<SearchEngineSpdProduct> spdProducts = Lists.newArrayListWithCapacity(searchProducts.size());
                for (SearchEngineProduct searchProduct : searchProducts) {
                    spdProducts.add(new SearchEngineSpdProduct()
                            .setSpdId(id)
                            .setSpdBrandCode(brandCode)
                            .setSpdAeCode(aeCode)
                            .setProductId(searchProduct.getProductId())
                            .setProductStatusId(ProductStatusEnum.PRODUCT_ADDED.toString())
                            .setProductSalesCount(Optional.ofNullable(searchProduct.getSalesCount()).orElse(0))
                            .setStoreId(searchProduct.getStoreId())
                            .setStoreName(searchProduct.getStoreName()));
                }
                result.setProducts(spdProducts);
            } else {
                result.setProducts(Collections.emptyList());
            }
        }
    }

    private void assembleResultByQueryProductInfoParallel(List<SearchEngineSpdProductResult> results,
                                                          String userLoginId,
                                                          String companyId,
                                                          String userGeoId,
                                                          List<String> productIds) {
        try {
            CompletableFuture<Map<String, PromoteProductAscriptionDTO>> activityInfoFuture = getActivityInfoFuture(userLoginId, companyId, productIds);
            CompletableFuture<Map<String, ActivityProductDTO>> activityProductFuture = getActivityProductFuture(activityInfoFuture);
            CompletableFuture<Map<String, ProductPrice>> productPriceFuture = getProductPriceFuture(activityInfoFuture, userLoginId, companyId, productIds);
            CompletableFuture<Map<String, ProductInventoryResult>> productInventoryFuture = getProductInventoryFuture(activityInfoFuture, userLoginId, companyId, userGeoId, productIds);
            CompletableFuture<Map<String, Product>> productFuture = getProductFuture(productIds);
            CompletableFuture<Map<String, List<ProductAttributeDO>>> productAttrFuture = getProductAttrFuture(productIds);
            CompletableFuture.allOf(activityInfoFuture,
                    activityProductFuture,
                    productPriceFuture,
                    productInventoryFuture,
                    productFuture,
                    productAttrFuture).join();
            Map<String, PromoteProductAscriptionDTO> activityInfoByProductId = activityInfoFuture.get();
            Map<String, ActivityProductDTO> activityProductByProductId = activityProductFuture.get();
            Map<String, ProductPrice> productPriceByProductId = productPriceFuture.get();
            Map<String, ProductInventoryResult> productInventoryByProductId = productInventoryFuture.get();
            Map<String, Product> productByProductId = productFuture.get();
            Map<String, List<ProductAttributeDO>> productAttributesByProductId = productAttrFuture.get();
            //组装/过滤商品数据
            for (SearchEngineSpdProductResult result : results) {
                List<SearchEngineSpdProduct> spdProducts = result.getProducts();
                if (CollectionUtils.isEmpty(spdProducts)) {
                    continue;
                }
                Iterator<SearchEngineSpdProduct> iterator = spdProducts.iterator();
                while (iterator.hasNext()) {
                    SearchEngineSpdProduct spdProduct = iterator.next();
                    String productId = spdProduct.getProductId();
                    Product product = productByProductId.get(productId);
                    ProductPrice productPrice = productPriceByProductId.get(productId);
                    ProductInventoryResult productInventoryResult = productInventoryByProductId.get(productId);
                    PromoteProductAscriptionDTO activityInfo = activityInfoByProductId.get(productId);
                    //商品基本信息/价格/库存，查不到视为无效商品直接过滤
                    if (product == null || productPrice == null || productInventoryResult == null
                            || Optional.ofNullable(productInventoryResult.getTotal()).orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) <= 0) {
                        iterator.remove();
                        continue;
                    }

                    //商品销售限制
                    List<ProductAttributeDO> productAttrs = productAttributesByProductId.get(productId);
                    if (CollectionUtils.isNotEmpty(productAttrs)) {
                        for (ProductAttributeDO attr : productAttrs) {
                            String attrType = attr.getAttrType();
                            String attrName = attr.getAttrName();
                            String attrValue = attr.getAttrValue();
                            if ("FULL_SALE".equals(attrType) || "SALES_VOLUME".equals(attrType)) {
                                SearchEngineSpdProductSalesLimit salesLimit = new SearchEngineSpdProductSalesLimit();
                                salesLimit.setType(attrType);
                                salesLimit.setValue(Integer.parseInt(attrValue));
                                spdProduct.setProductSalesLimit(salesLimit);
                                continue;
                            }
                            if ("生产年份".equals(attrName)) {
                                spdProduct.setProductProduceYear(attrValue);
                            }
                        }
                    }

                    //商品展示名称，这里应该和品类有关，后续增加其他品类时，根据要求扩展
                    spdProduct.setProductName(product.getProductName());
                    spdProduct.setProductDesc(product.getDescription());
                    spdProduct.setProductNameExtend(product.getDetailScreen());
                    ActivityProductDTO activityProduct = activityProductByProductId.get(productId);
                    if (activityProduct != null) {
                        spdProduct.setProductName(activityProduct.getActProductName());
                        spdProduct.setProductDesc(activityProduct.getActDescription());
                    }
                    StringBuilder productDisplayNameSb = new StringBuilder();
                    String productProduceYear = spdProduct.getProductProduceYear();
                    String productNameExtend = spdProduct.getProductNameExtend();
                    String productDesc = spdProduct.getProductDesc();
                    String productName = spdProduct.getProductName();
                    if (StringUtils.isNotBlank(productProduceYear)) {
                        productDisplayNameSb.append(productProduceYear).append(" ");
                    }
                    if (StringUtils.isNotBlank(productNameExtend)) {
                        productDisplayNameSb.append(productNameExtend);
                    } else if (StringUtils.isNotBlank(productDesc)) {
                        productDisplayNameSb.append(productDesc);
                    } else {
                        String newProductName = null;
                        if (StringUtils.isNotBlank(productProduceYear)) {
                            newProductName = productName.replace(productProduceYear, "");
                        } else {
                            newProductName = productName;
                        }
                        productDisplayNameSb.append(newProductName);
                    }
                    spdProduct.setProductDisplayName(StringUtils.trim(productDisplayNameSb.toString()));

                    //商品价格
                    SearchEngineSpdProductPrice price = new SearchEngineSpdProductPrice();
                    price.setProductId(productId);
                    price.setHasPriceTax(!"NO".equals(productPrice.getOpenInvoiceType()));
                    price.setPrice(productPrice.getPrice().setScale(2, RoundingMode.HALF_UP));
                    price.setTaxRate(productPrice.getTaxRate());
                    List<ProductStepPrice> stepPrices = productPrice.getStepPrices();
                    if (CollectionUtils.isNotEmpty(stepPrices)) {
                        price.setStepPrices(BeanMapper.mapList(stepPrices, SearchEngineSpdProductPrice.StepPrice.class));
                        price.setPriceType("STEP");
                    } else {
                        price.setPriceType("SINGLE");
                    }
                    spdProduct.setProductPrice(price);

                    //商品活动
                    if (activityInfo != null) {
                        SearchEngineSpdProductActivity spdProductActivity = new SearchEngineSpdProductActivity();

                        //活动类型：TIME_LIMIT_SECKILL-限时秒杀，ZONE_PROMOTION-分区促销，GROUP_BUY-拼团
                        String type = "";
                        SearchEngineSpdProductActivity.SecKillInfo secKillInfo = new SearchEngineSpdProductActivity.SecKillInfo();
                        SearchEngineSpdProductActivity.PromotionInfo promotionInfo = new SearchEngineSpdProductActivity.PromotionInfo();
                        SearchEngineSpdProductActivity.GroupBuyInfo groupBuyInfo = new SearchEngineSpdProductActivity.GroupBuyInfo();

                        String activityType = activityInfo.getType();
                        if ("TIME_LIMIT_SECKILL".equals(activityType)) {
                            type = "SEC_KILL";
                            String id = activityInfo.getId();
                            if (StringUtils.isNotBlank(id)) {
                                String[] split = id.split("@");
                                if (split.length > 0) {
                                    secKillInfo.setId(split[0]);
                                }
                            }
                            secKillInfo.setPlaceId(activityInfo.getPlaceId());
                            secKillInfo.setSessionId(activityInfo.getSessionId());
                            secKillInfo.setTotalNumber(activityInfo.getTotalNumber());
                        } else if ("ZONE_PROMOTION".equals(activityType)) {
                            type = "PROMOTE";
                            String id = activityInfo.getId();
                            if (StringUtils.isNotBlank(id)) {
                                String[] split = id.split("@");
                                if (split.length > 0) {
                                    secKillInfo.setId(split[0]);
                                }
                            }
                            promotionInfo.setPlaceId(activityInfo.getPlaceId());
                        } else if ("GROUP_BUY".equals(activityType)) {
                            type = "GROUP_BUY";
                            groupBuyInfo.setGroupBuyId(activityInfo.getGroupBuyId());
                        }
                        if (StringUtils.isNotBlank(type)) {
                            price.setPriceType(type);
                            spdProductActivity.setType(type);
                            spdProductActivity.setSecKillInfo(secKillInfo);
                            spdProductActivity.setPromotionInfo(promotionInfo);
                            spdProductActivity.setGroupBuyInfo(groupBuyInfo);
                        }
                        spdProduct.setProductActivity(spdProductActivity);
                    }

                    //商品库存
                    List<ProductInventory> inventories = productInventoryResult.getInventories();
                    if (CollectionUtils.isEmpty(inventories)) {
                        iterator.remove();
                        continue;
                    }
                    List<SearchEngineSpdProductInventory> spdProductInventories = Lists.newArrayListWithCapacity(inventories.size());
                    for (ProductInventory inventory : inventories) {
                        BigDecimal inventoryValue = new BigDecimal(inventory.getInventoryValue());
                        if (inventoryValue.compareTo(BigDecimal.ZERO) <= INT_ZERO) {
                            continue;
                        }
                        spdProductInventories.add(new SearchEngineSpdProductInventory()
                                .setProductId(productId)
                                .setFacilityId(inventory.getFacilityId())
                                .setFacilityName(inventory.getFacilityName())
                                .setFacilityGeoId(inventory.getGeoId())
                                .setFacilityLatitude(inventory.getFacilityLatitude())
                                .setFacilityLongitude(inventory.getFacilityLongitude())
                                .setFacilityDistance(inventory.getFacilityDistance())
                                .setInventoryValue(inventoryValue));
                    }
                    if (CollectionUtils.isEmpty(spdProductInventories)) {
                        iterator.remove();
                        continue;
                    }
                    spdProduct.setProductInventories(spdProductInventories);
                }
            }
        } catch (Exception e) {
            log.error(SPD_PRODUCT_LOG_PREFIX + "查询商品信息失败", e);
        }
    }

    private CompletableFuture<Map<String, PromoteProductAscriptionDTO>> getActivityInfoFuture(String userLoginId,
                                                                                              String companyId,
                                                                                              List<String> productIds) {
        return CompletableFuture.supplyAsync(() ->
                promoteFacade.getActivityInfoByProductId(userLoginId, companyId, productIds), executor);
    }

    private CompletableFuture<Map<String, ActivityProductDTO>> getActivityProductFuture(CompletableFuture<Map<String, PromoteProductAscriptionDTO>> activityInfoFuture) {
        return activityInfoFuture.thenApplyAsync(map -> {
            Set<String> actProductId = map.keySet();
            if (CollectionUtils.isEmpty(actProductId)) {
                return Collections.emptyMap();
            }
            return activityProductFacade.listProductInfoByIds(new ArrayList<>(map.keySet()))
                    .stream()
                    .collect(Collectors.toMap(ActivityProductDTO::getProductId, Function.identity(), (k1, k2) -> k1));
        }, executor);
    }

    private CompletableFuture<Map<String, ProductPrice>> getProductPriceFuture(CompletableFuture<Map<String, PromoteProductAscriptionDTO>> activityInfoFuture,
                                                                               String userLoginId,
                                                                               String companyId,
                                                                               List<String> productIds) {
        return activityInfoFuture.thenApplyAsync(map -> {
            ProductPriceRequest priceRequest = new ProductPriceRequest();
            List<ProductPriceRequest.Query> priceQueries = Lists.newArrayList();
            for (String productId : productIds) {
                ProductPriceRequest.Query query = new ProductPriceRequest.Query();
                query.setProductId(productId);
                PromoteProductAscriptionDTO activityInfo = map.get(productId);
                if (activityInfo != null) {
                    query.setActivityId(activityInfo.getId());
                }
                priceQueries.add(query);
            }
            priceRequest.setUserLoginId(userLoginId).setCompanyId(companyId).setQueries(priceQueries);
            return restfulProductPrice.listProductPricesByRequest(priceRequest).stream()
                    .collect(Collectors.toMap(ProductPrice::getProductId, Function.identity(), (k1, k2) -> k1));
        }, executor);
    }

    private CompletableFuture<Map<String, ProductInventoryResult>> getProductInventoryFuture(CompletableFuture<Map<String, PromoteProductAscriptionDTO>> activityInfoFuture,
                                                                                             String userLoginId,
                                                                                             String companyId,
                                                                                             String userGeoId,
                                                                                             List<String> productIds) {
        return activityInfoFuture.thenApplyAsync(map -> {
            ProductInventoryResultRequest inventoryRequest = new ProductInventoryResultRequest();
            inventoryRequest.setUserLoginId(userLoginId);
            inventoryRequest.setCompanyId(companyId);
            List<ProductInventoryResultRequest.Query> queries = Lists.newArrayList();
            for (String productId : productIds) {
                ProductInventoryResultRequest.Query query = new ProductInventoryResultRequest.Query();
                query.setProductId(productId);
                PromoteProductAscriptionDTO activityInfo = map.get(productId);
                if (activityInfo != null) {
                    query.setActivityId(activityInfo.getId());
                }
                queries.add(query);
            }
            inventoryRequest.setQueries(queries);
            inventoryRequest.setUserAddressGeoId(userGeoId);
            return productInventory.listProductInventoryResultsByRequest(inventoryRequest).stream()
                    .collect(Collectors.toMap(ProductInventoryResult::getProductId, Function.identity(), (k1, k2) -> k1));
        }, executor);
    }

    private CompletableFuture<Map<String, Product>> getProductFuture(List<String> productIds) {
        return CompletableFuture.supplyAsync(() ->
                productMapper.findProductListById(productIds).stream()
                        .collect(Collectors.toMap(Product::getProductId, Function.identity(), (k1, k2) -> k1)), executor);
    }

    private CompletableFuture<Map<String, List<ProductAttributeDO>>> getProductAttrFuture(List<String> productIds) {
        return CompletableFuture.supplyAsync(() ->
                productAttributeMapper.listAttributeByProductIds(productIds).stream()
                        .collect(Collectors.groupingBy(ProductAttributeDO::getProductId)), executor);
    }

    private void sortResultBySalesCount(List<SearchEngineSpdProductResult> results,
                                        String sortType,
                                        List<String> productIds) {
        for (SearchEngineSpdProductResult result : results) {
            List<SearchEngineSpdProduct> products = result.getProducts();
            if (CollectionUtils.isNotEmpty(products)) {
                //products.sort(Comparator.comparing(SearchEngineSpdProduct::getProductSalesCount, Comparator.nullsLast(Integer::compareTo)));
                if (SPD_PRODUCT_SORT_TYPE_SALES_COUNT_DESC.equals(sortType)) {
                    int size = products.size();
                    if (size > SPD_PRODUCT_MAX_NUM) {
                        result.setProducts(products.subList(INT_ZERO, SPD_PRODUCT_MAX_NUM));
                    }
                }
                productIds.addAll(result.getProducts().stream()
                        .map(SearchEngineSpdProduct::getProductId).collect(Collectors.toList()));
            }
        }

    }

    private void sortResultBySortType(List<SearchEngineSpdProductResult> results,
                                      String sortType) {
        for (SearchEngineSpdProductResult result : results) {
            List<SearchEngineSpdProduct> spdProducts = result.getProducts();
            if (CollectionUtils.isNotEmpty(spdProducts)) {
                if (SPD_PRODUCT_SORT_TYPE_PRICE_ASC.equals(sortType)) {
                    spdProducts.sort(Comparator.comparing(o -> o.getProductPrice().getPrice(), Comparator.nullsLast(BigDecimal::compareTo)));
                } else if (SPD_PRODUCT_SORT_TYPE_DISTANCE_ASC.equals(sortType)
                        || SPD_PRODUCT_SORT_TYPE_DEFAULT.equals(sortType)) {
                    spdProducts.sort(Comparator.comparing(o -> o.getProductInventories().get(0).getFacilityDistance(),
                            Comparator.nullsLast(BigDecimal::compareTo)));
                }
                int size = spdProducts.size();
                if (size > SPD_PRODUCT_MAX_NUM) {
                    result.setProducts(spdProducts.subList(INT_ZERO, SPD_PRODUCT_MAX_NUM));
                }
            }
        }
    }
    //endregion
}
