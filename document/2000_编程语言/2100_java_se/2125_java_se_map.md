---
title: java_se_map
date: 2018-10-29 00:00:00
---
## Map<K,V>接口
* 偶对象保存的最大父接口
* 以下源码基于jdk1.8

## HashMap

HashMap的线程不安全：
>（1）1.7
>put时采用链表头插法，所以扩容时会链表会反转一次，并发插入时可能导致死循环

``` java
//例如：1->2
//扩容
//从头到尾遍历，每次都是头插法
//当2个线程同时都走到扩容的while循环里
//2个变量分别指向 e=1、next=2、table[index]=null
//线程A挂起
//线程B继续执行扩容完成，2->1
//线程A继续执行，第一遍执行完
//第二遍刚进来，e=2、next=1、table[index]=1
//第三遍刚进来，e=1、next=null、table[index]=2
//继续执行到 e.next = newTable[i];就会给 1->2
//此时就形成了一个环形链表 1->2->1

void transfer(Entry[] newTable, boolean rehash) {
    int newCapacity = newTable.length;
    for (Entry<K,V> e : table) {
        while(null != e) {
            //关键代码
            Entry<K,V> next = e.next;
            if (rehash) {
                e.hash = null == e.key ? 0 : hash(e.key);
            }
            int i = indexFor(e.hash, newCapacity);
            //关键代码
            e.next = newTable[i];
            newTable[i] = e;
            e = next;
        }
    }
}
```

>为什么采用头插法？
>可能的原因：最近访问过的数据大概率会再次访问，所以放在前面
>
>（1.8）
>改为链表尾插法，解决1.7的问题 
>但是在链接新的结点时，比较和链接结点是2个操作，非原子的，所以线程不安全
> 
>共同的线程不安全：
>数组和Node结点的值不能保证可见性，所以扩容或者修改结点的值后，其他线程不能看到最新的
>put时，拿到结点和往结点后面链接是2个操作，容易丢失更新


### 类定义
``` java
public class HashMap<K,V> extends AbstractMap<K,V> 
implements Map<K,V>, Cloneable, Serializable {}
```

### 常量
``` java
//java序列化版本号
private static final long serialVersionUID = 362498820763181265L;
//默认初始哈希表数组长度：2^4=16
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
//最大哈希表数组长度：2^30，int4个字节，最大值为2^32-1
static final int MAXIMUM_CAPACITY = 1 << 30;
//默认负载因子
//概率问题，泊松分布
//当用0.75时，桶中元素能到达 8 个的时候，概率已经变得非常小
static final float DEFAULT_LOAD_FACTOR = 0.75f;
//单链表转化红黑树临界值
static final int TREEIFY_THRESHOLD = 8;
//红黑树转化单链表临界值
static final int UNTREEIFY_THRESHOLD = 6;
//最小树形化容量临界值，当哈希表的数组长度大于此临界值时才允许转化为红黑树
static final int MIN_TREEIFY_CAPACITY = 64;
```

### 字段
``` java
//哈希表，延迟初始化
transient Node<K,V>[] table;
//只有行为没有状态的一个用来遍历map中数据的载体（静态内部类）
transient Set<Map.Entry<K,V>> entrySet;
//元素个数
transient int size;
//增/删/改 次数，用于迭代遍历发现变动后快速失败
transient int modCount;
//哈希表数组扩容元素个数临界值 = 当前哈希表数组长度 * 负载因子
int threshold;
//负载因子：默认0.75
final float loadFactor;
```

### 内部类
``` java
//哈希表单链表的结点（不完全）
static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
}
//哈希表红黑树的结点（不完全）
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
}
```

### 构造方法
``` java
public HashMap() {}
public HashMap(int initialCapacity) {}
public HashMap(int initialCapacity, float loadFactor) {}
public HashMap(Map<? extends K, ? extends V> m) {}
```
### 关键源码（增/删/改/查）

#### 增：put方法
``` java
public V put(K key, V value) {
    //调用hash方法和putVal方法
    return putVal(hash(key), key, value, false, true);
}


//hash方法：又叫扰动函数，计算链表/红黑树结点的hash字段的值

//1、key.hashCode()，调用key的hashCode()方法得到key的哈希值
//2、复习3个位运算符
//    &与运算：两个数相应位均为1结果为1，其他均为0
//    |或运算：两个数相应位都为0结果为0，其他均为1
//    ^异或运算：两个数相应位相同则为0，不同则为1
//3、key的哈希值是int32位的值，将哈希值右移16位得到（16个0+原哈希值高16位），
//    再将（16个0+原哈希值高16位）和原哈希值做异或运算，
//    相当于原哈希值的高16位和16个0、低16位和高16位做异或运算，以此来加大hash值低位的随机性
//4、为什么要加大hash值低位的随机性？
//    主要是因为防止key的散列函数本身做得不好，分布上成等差数列等漏洞，恰好使最后几个低位呈现规律性重复，
//    而HashMap的数组长度全部取2的整次幂，这样（数组长度-1）正好相当于一个“低位掩码”，
//    “与”操作的结果就是散列值的高位全部归零，只保留低位值，用来做HashMap数组下标（不会出现超出数组索引的情况），
//    如果hash值的后几位重复太多就会造成数据存放在HashMap数组不够随机均匀
//
//5、示例，HashMap数组第一次的初始容量为 n = 1<<4 = 16
//  h = hashCode()    :1010 1101 1111 1011 1111 1101 1000 1111
//  h >>> 16          :0000 0000 0000 0000 1010 1101 1111 1011
//  hash=h^(h>>>16)   :0101 0010 1111 1011 0101 0000 0111 0100
//  n-1 = 16-1 = 15   :0000 0000 0000 0000 0000 0000 0000 1111
//  hash & (n-1)      :0000 0000 0000 0000 0000 0000 0000 0100
//  数组下标为 0100 = 4

static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}



//putVal方法
final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    //HashMap数组为空或者其长度为0时，即构造函数执行后第一次put数据时
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;//resize方法进行数组扩容
    //数组中的计算出的索引的位置还没有数据，即第一次往这个位置放数据
    //第一次放，所以直接放在这个位置
    //(n - 1) & hash就是上述的取数组下标操作
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);//newNode方法构造一个Node结点
    //数组的这个索引位置已经有数据了
    else {
        Node<K,V> e; K k;
        //新放入的数据是这个位置的头结点
        //条件：头结点的hash和新放入数据的hash相同 
        //并且 （key的引用地址相同 或者 equals判断相同）
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        //新放入的数据是红黑树的其他结点
        else if (p instanceof TreeNode)
            //插入数组这个位置的红黑树
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        //新放入的数据是单链表的其他结点
        else {
            //从数组索引处的头结点的next开始遍历，e是游标
            for (int binCount = 0; ; ++binCount) {
                //如果是单链表最后一个结点，直接插入，尾插法
                //这里线程不安全，比较和链接新结点非原子的
                //可能导致数据覆盖
                if ((e = p.next) == null) {
                    //构造新数据结点
                    p.next = newNode(hash, key, value, null);
                    //TREEIFY_THRESHOLD值为8，即单链表长度>=8时
                    if (binCount >= TREEIFY_THRESHOLD - 1)
                        treeifyBin(tab, hash);//把单链表转化为红黑树
                    break;//跳出循环
                }
                //如果在单链表中找到要插入的数据
                //条件：头结点的hash和新放入数据的hash相同 
                //并且 （key的引用地址相同 或者 equals判断相同）
                if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                    break;//跳出循环
                p = e;
            }
        }
        //上面找到了 新插入数据 为已存在的数据，将只更新value
        if (e != null) {
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            //模板方法模式，用于LinkedHashMap使用
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    //如果当前的元素个数（逻辑长度） 超过 扩容元素个数临界值，执行数组扩容
    if (++size > threshold)
        resize();
    //模板方法模式，用于LinkedHashMap使用
    afterNodeInsertion(evict);
    return null;
}



//扩容方法：
final Node<K,V>[] resize() {
    //旧哈希表数组
    Node<K,V>[] oldTab = table;
    //旧哈希表数组长度
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    //旧扩容元素个数临界值
    int oldThr = threshold;
    //新哈希表数组长度，新扩容元素个数临界值
    int newCap, newThr = 0;

    if (oldCap > 0) {
        //旧哈希表数组长度 > 0
        //旧哈希表数组长度 >= 最大哈希表数组长度，不继续扩容
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        //新哈希表数组长度 = 旧哈希表数组长度 * 2
        //边界值：oldCap = 2^29，newCap = 2^30，将不满足此条件，
        //所以 newThr 未赋值，依旧为0，后面有特殊处理
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY){
            //新扩容元素个数临界值 = 旧扩容元素个数临界值 * 2
            newThr = oldThr << 1;
        }
    }
    else if (oldThr > 0) // initial capacity was placed in threshold
        //旧扩容元素个数临界值 > 0，说明第一次put元素，构造方法是
        //public HashMap(int initialCapacity, float loadFactor) {}
        //这个构造方法的实现里面计算了threshold，但是哈希表数组还没有初始化
        //新哈希表数组长度 = 旧扩容元素个数临界值（后面会标准化为2的指数幂）
        newCap = oldThr;
    else {
        //旧哈希表数组长度为0，说明第一次put元素，非负载因子的构造方法
        //新哈希表数组长度 = 默认初始哈希表数组长度
        //新扩容元素个数临界值 = 默认负载因子 * 默认初始哈希表数组长度
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }

    //上述oldCap > 0里边的newCap的边界值 oldCap = 2^29，newCap = 2^30 的特殊处理
    //防止（新哈希表数组长度，新扩容元素个数临界值）int溢出
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }

    //扩容元素个数临界值：赋值新的
    threshold = newThr;
    //哈希表数组：创新新长度的哈希表数组
    @SuppressWarnings({"rawtypes","unchecked"})
    Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
    
    //如果旧哈希表数组不是null，则需要重新计算存放原来的元素
    if (oldTab != null) {
        //遍历旧哈希表数组
        for (int j = 0; j < oldCap; ++j) {
            //从索引为0开始
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {
                //索引处的头结点不为null，说明有数据
                oldTab[j] = null;
                if (e.next == null)
                    //只有一个头结点，直接计算新的索引赋值
                    newTab[e.hash & (newCap - 1)] = e;
                else if (e instanceof TreeNode)
                    //红黑树结点
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                else { // preserve order
                    //这部分是为了将索引处的一个链表拆分成两个链表
                    //扩容后这条链表拆分后只会分布在2个索引位置处：
                    //老索引位置 和 （老索引位置+老哈希表数组长度）
                  
                    // 1）新索引位置 = 老索引位置
                    // 老索引位置计算：
                    // hash                :0101 0010 1111 1011 0101 0000 0100 0100
                    // n-1 = 16-1 = 15     :0000 0000 0000 0000 0000 0000 0000 1111
                    // index = hash & (n-1):0000 0000 0000 0000 0000 0000 0000 0100
                    // index = 4
                    
                    // 新索引位置计算：
                    // hash                :0101 0010 1111 1011 0101 0000 0100 0100
                    // n-1 = 32-1 = 31     :0000 0000 0000 0000 0000 0000 0001 1111
                    // index = hash & (n-1):0000 0000 0000 0000 0000 0000 0000 0100
                    // index = 4 = 4（老索引位置）

                    // hash & 老哈希表数组长度
                    // 等于0，新索引位置 = 老索引位置
                    // hash                :0101 0010 1111 1011 0101 0000 0100 0100
                    // n= 16               :0000 0000 0000 0000 0000 0000 0001 0000
                    // hash & n = 0        :0000 0000 0000 0000 0000 0000 0000 0000


                    // 2）新索引位置 = 老索引位置 + 老哈希表数组长度
                    // 老索引位置计算：
                    // hash                :0101 0010 1111 1011 0101 0000 0111 0100
                    // n-1 = 16-1 = 15     :0000 0000 0000 0000 0000 0000 0000 1111
                    // index = hash & (n-1):0000 0000 0000 0000 0000 0000 0000 0100
                    // index = 4
                    
                    // 新索引位置计算：
                    // hash                :0101 0010 1111 1011 0101 0000 0111 0100
                    // n-1 = 32-1 = 31     :0000 0000 0000 0000 0000 0000 0001 1111
                    // index = hash & (n-1):0000 0000 0000 0000 0000 0000 0001 0100
                    // index = 20 = 4（老索引位置） + 16(老哈希表数组长度)

                    // hash & 老哈希表数组长度
                    // 不等于0，新索引位置 = 老索引位置 + 老哈希表数组长度
                    // hash                :0101 0010 1111 1011 0101 0000 0111 0100
                    // n= 16               :0000 0000 0000 0000 0000 0000 0001 0000
                    // hash & n = 16       :0000 0000 0000 0000 0000 0000 0001 0000


                    //1）新索引位置 = 老索引位置 的链表
                    Node<K,V> loHead = null, loTail = null;
                    //2）新索引位置 = 老索引位置 + 老哈希表数组长度 的链表
                    Node<K,V> hiHead = null, hiTail = null;

                    Node<K,V> next;
                    do {
                        //注意：这里是从头结点一直遍历到尾结点
                        //所以构建的两个新链表时也是有序的（和老链表顺序一致）
                        next = e.next;
                        
                        // hash & 老哈希表数组长度
                        // 不等于0，新索引位置 = 老索引位置 + 老哈希表数组长度
                        // 等于0，新索引位置 = 老索引位置
                        
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);

                    //链接两个新链表到对应的索引位置处
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}

```

#### 删：remove方法
``` java
public V remove(Object key) {
    Node<K,V> e;
    //调用hash方法和removeNode方法
    return (e = removeNode(hash(key), key, null, false, true)) == null ?
        null : e.value;
}

//removeNode方法
final Node<K,V> removeNode(int hash, Object key, Object value,
                           boolean matchValue, boolean movable) {
    Node<K,V>[] tab; Node<K,V> p; int n, index;
    //key存在的必要条件校验
    //哈希表数组不为null，哈希表数组长度>0，key对应的哈希表数组索引处有数据
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (p = tab[index = (n - 1) & hash]) != null) {
        //node和p都是游标，p.next=node
        //node是key对应的结点
        Node<K,V> node = null, e; K k; V v;
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            //key对应的结点是头结点
            node = p;
        else if ((e = p.next) != null) {
            //遍历红黑树拿到对应的结点
            if (p instanceof TreeNode)
                node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
            else {
                //遍历链表拿到对应的结点
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key ||
                         (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
        }
        //找到了key对应的结点
        if (node != null && (!matchValue || (v = node.value) == value ||
                             (value != null && value.equals(v)))) {
            //红黑树结点
            if (node instanceof TreeNode)
                ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
            //链表头结点
            else if (node == p)
                tab[index] = node.next;
            //其他结点
            else
                p.next = node.next;
            ++modCount;
            --size;
            //模板方法模式，LinkedHashMap使用
            afterNodeRemoval(node);
            return node;
        }
    }
    return null;
}
```

#### 改：put方法

#### 查：get方法
``` java
public V get(Object key) {
    Node<K,V> e;
    //调用hash方法和getNode方法
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}

//getNode方法
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    //key存在的必要条件校验
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        //头结点
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                //红黑树
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                //链表
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

<br/>
<hr/>

## LinkedHashMap

### 类定义
``` java
//继承HashMap
public class LinkedHashMap<K,V>
    extends HashMap<K,V> implements Map<K,V> {}
```

### 字段
``` java
//java序列化版本号
private static final long serialVersionUID = 3801124242820219131L;
//双链表头结点
transient LinkedHashMap.Entry<K,V> head;
//双链表尾结点
transient LinkedHashMap.Entry<K,V> tail;
//accessOrder：true访问顺序排序（LRU，最后访问的排在后面），flase插入顺序排序（最后插入的排在后面，默认值）
final boolean accessOrder;
```

### 内部类
``` java
//双向链表结点
static class Entry<K,V> extends HashMap.Node<K,V> {
    //前结点，后结点
    Entry<K,V> before, after;
    Entry(int hash, K key, V value, Node<K,V> next) {
        super(hash, key, value, next);
    }
}
```

### 构造方法
``` java
public LinkedHashMap(){}
public LinkedHashMap(int initialCapacity) {}
public LinkedHashMap(int initialCapacity, float loadFactor) {}
public LinkedHashMap(Map<? extends K, ? extends V> m) {}
public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {}
```

### 关键源码
``` java
//继承HashMap
//只是维护了一个双向链表来保证有序
//双向链表的操作通过重写方法，使用模板方法设计模式实现的

//重写，创建新结点，accessOrder（访问顺序排序/插入顺序排序）都需要将新结点链接到双向链表的尾部
Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
    LinkedHashMap.Entry<K,V> p =
        new LinkedHashMap.Entry<K,V>(hash, key, value, e);
    linkNodeLast(p);
    return p;
}

//重写，删除双向链表中的元素
void afterNodeRemoval(Node<K,V> e) { // unlink
    LinkedHashMap.Entry<K,V> p =
        (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
    p.before = p.after = null;
    if (b == null)
        head = a;
    else
        b.after = a;
    if (a == null)
        tail = b;
    else
        a.before = b;
}

//重写，用于插入新的元素后是否需要做特殊处理
//例如LRU，需要保证map中的元素个数，则可以删除双向链表的头结点
void afterNodeInsertion(boolean evict) { // possibly remove eldest
    LinkedHashMap.Entry<K,V> first;
    //removeEldestEntry()默认未实现，直接返回false
    if (evict && (first = head) != null && removeEldestEntry(first)) {
        K key = first.key;
        removeNode(hash(key), key, null, false, true);
    }
}

//重写，accessOrder（为true，访问顺序排序）时，移动结点到尾部
void afterNodeAccess(Node<K,V> e) { // move node to last
    LinkedHashMap.Entry<K,V> last;
    if (accessOrder && (last = tail) != e) {
        LinkedHashMap.Entry<K,V> p =
            (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
        p.after = null;
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a != null)
            a.before = b;
        else
            last = b;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
        tail = p;
        ++modCount;
    }
}
```

<br/>
<hr/>

## HashTable

### 关键源码
``` java
//基本上所有的public方法都加了 synchronized 关键字，来保证线程安全
//设计上也没有 HashMap 精巧，这里只列几个点和 HashMap 对比

//默认哈希表数组长度为11
//负载因子为0.75
public Hashtable() {
    this(11, 0.75f);
}

//put方法
//哈希算法(计算得到数组索引) = (hashCode & int最大正值) % 哈希表数组长度
public synchronized V put(K key, V value) {
    // Make sure the value is not null
    if (value == null) {
        throw new NullPointerException();
    }

    // Makes sure the key is not already in the hashtable.
    Entry<?,?> tab[] = table;
    int hash = key.hashCode();
    //哈希算法
    int index = (hash & 0x7FFFFFFF) % tab.length;
    @SuppressWarnings("unchecked")
    Entry<K,V> entry = (Entry<K,V>)tab[index];
    for(; entry != null ; entry = entry.next) {
        if ((entry.hash == hash) && entry.key.equals(key)) {
            V old = entry.value;
            entry.value = value;
            return old;
        }
    }

    addEntry(hash, key, value, index);
    return null;
}

//扩容方法
//新哈希表数组长度 = 旧哈希表数组长度 * 2 + 1
protected void rehash() {
    int oldCapacity = table.length;
    Entry<?,?>[] oldMap = table;

    // overflow-conscious code
    int newCapacity = (oldCapacity << 1) + 1;
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
        if (oldCapacity == MAX_ARRAY_SIZE)
            // Keep running with MAX_ARRAY_SIZE buckets
            return;
        newCapacity = MAX_ARRAY_SIZE;
    }
    Entry<?,?>[] newMap = new Entry<?,?>[newCapacity];

    modCount++;
    threshold = (int)Math.min(newCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
    table = newMap;

    for (int i = oldCapacity ; i-- > 0 ;) {
        for (Entry<K,V> old = (Entry<K,V>)oldMap[i] ; old != null ; ) {
            Entry<K,V> e = old;
            old = old.next;
            int index = (e.hash & 0x7FFFFFFF) % newCapacity;
            e.next = (Entry<K,V>)newMap[index];
            newMap[index] = e;
        }
    }
}
```

<br/>
<hr/>

## TreeMap
``` java
//红黑树实现的可自定义排序的map
//待补充
```

<br/>
<hr/>

## ConcurrentHashMap
详见 jc_并发容器篇