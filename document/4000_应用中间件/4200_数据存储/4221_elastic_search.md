---
title: elastic_search
date: 2020-04-01 00:00:00
---

TODO

## dsl常见语法

``` json
must: (表达式1 && 表达式2)
must_not: !(表达式1 && 表达式2)
should: (表达式1 || 表达式2)
filter: 过滤，不参与评分


match_all
match
multi_match

range

term
terms

exists
missing

{
	"bool": {
		"must": {
			"match": {
				"title": "how to make millions"
			}
		},
		"must_not": {
			"match": {
				"tag": "spam"
			}
		},
		"should": [
			{
				"match": {
					"tag": "starred"
				}
			}
		],
		"filter": {
			"bool": {
				"must": [
					{
						"range": {
							"date": {
								"gte": "2014-01-01"
							}
						}
					},
					{
						"range": {
							"price": {
								"lte": 29.99
							}
						}
					}
				],
				"must_not": [
					{
						"term": {
							"category": "ebooks"
						}
					}
				]
			}
		}
	}
}


```