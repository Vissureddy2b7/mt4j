# MT4J [![License: MIT](https://img.shields.io/badge/License-MIT%202.0-0298c3.svg)](https://opensource.org/licenses/MIT) ![Build](https://github.com/triodjangopiter/bridge/workflows/Build/badge.svg) [![codecov](https://codecov.io/gh/PavelRavvich/mt4j/branch/master/graph/badge.svg?token=Y3IRTX7LPW)](https://codecov.io/gh/PavelRavvich/mt4j) [![Issues](https://img.shields.io/github/issues/triodjangopiter/mt4j)](https://opensource.org/licenses/MIT)

## About

MT4J is software for integration between Meta Trader 5 and Java implemented through REST API.

Using this application you can write trading strategies with Java instead of mql5 language. For implementation strategy logic you can 
use already connected [ta4j] technical indicators library, or connect something else what you prefer using another maven artifact with `pom.xml`.

The server based on Tomcat and use port **80** (default) or **443**. Ports range can't be changed by specific Meta Trader 5 reasons, see more in MQL5 documentation: [https://www.mql5.com/en/docs/network/webrequest][webrequest].

MetaTrader 5 required enabling permission settings for send http requests to any hosts, see more in MQL5 documentation: [https://www.mql5.com/en/docs/network][settings]

## Installation
### Step 1. 
Clone repository with `git clone https://github.com/PavelRavvich/mt4j.git` or download sources with GitHub web interface.
### Step 2
Copy library source folder from `mt5/Library/*` to `<META_TRADER_LOCATION>/MQL5/Include/Library/*`.

### Step 3
Copy advisor source file from `mt5/MT4J.mq5` to `<META_TRADER_LOCATION>/MQL5/Experts/MT4J.mq5`. 

### Step 4
Compile Java sources, and run Spring Boot server in any convenient way.

### Strategy implementation principals

For implement strategy, implement interface `Strategy` and for his two method:
* `String getName()` - strategy identifier (Should be equals advisor input *StrategyName*). 

You can have many strategies with different names, and no limited running `MT4J.mq5` simultaneously, and handle all of them with one server instance. For matching use equal advisor input *StrategyName* and `String getName()` return value.
You can see example of implemented strategy here: [https://github.com/PavelRavvich/mt4j/blob/master/src/main/java/pro/laplacelab/mt4j/example/Example.java][stategyExample]

* ` List<Signal> apply(Advisor advisor, Map<Timeframe, List<Rate>> rates)` - strategy logic implementation. 

All data of advisor's inputs, positions, rates, account you receive automatically. 

Strategy implementation class should be marked `@Component` annotation.

## How it's work
### Data Exchange Protocol

Endpoints:

**POST** `http://127.0.0.1/api/advisor/add` - save advisor and return UUID identifier.

_Request:_
```json
{
    "magic": 100000,
    "inputs": [
        {
            "key": "propName1",
            "value": "str",
            "type": "STRING"
        },
        {
            "key": "propName2",
            "value": 10,
            "type": "NUMBER"
        },
        {
            "key": "propName3",
            "value": true,
            "type": "BOOLEAN"
        }
    ]
}
```

_Response:_
```json
{
    "id": "c4eb34e4-c9c3-4b7e-856d-d5d00588464d"
}
```


**GET** `http://127.0.0.1/api/signal` - resolve indicator's data and actual state of open and closed positions, return signal generated by wired strategy.

_Request:_
```json
{
    "advisorId":"6a649280-2d72-4f4c-8457-0b0f9c43f244",
    "strategyName":"EXAMPLE",
    "account":{
        "id": 12345,
        "balance": 100.55,
        "margin": 100.55,
        "freeMargin": 100.55,
        "owner": "Ritchie Goldberg",
        "company": "Best Broker"
    },
    "positions":[
        {
            "isHistory": true,
            "type": "LONG | SHORT",
            "magic": 100000,
            "positionId": 100000,
            "lot": 0.01,
            "stopLoss": 100,
            "takeProfit": 100,
            "openAt": 16384394738,
            "openPrice": 1.23,
            "closePrice": 1.23,
            "closeAt": 16384394738,
            "swap": 1.23,
            "commission": 1.23,
            "profit": 123.45
        }
    ],
    "rates": {
        "M_1": [
            {
                "spread": 4,
                "time": 16384394738,
                "open": 1.12412,
                "high": 1.32412,
                "low": 1.32421,
                "close": 1.32111,
                "tickVolume": 12,
                "realVolume": 0
            }
        ]
    }
}
```


_Response_ 

Can contain 3 position types:
  * Open (buy or sell)
  * Close
  * Update (TakeProfit and StopLoss can be updated)
    
```json
[
    {
        "advisorId":"c4eb34e4-c9c3-4b7e-856d-d5d00588464d",
        "type":"BUY | SELL",
        "lot":0.01,
        "stopLoss":100,
        "takeProfit":100
    },
    {
        "advisorId":"c4eb34e4-c9c3-4b7e-856d-d5d00588464d",
        "type":"UPDATE",
        "positionId":10000000,
        "stopLoss":100,
        "takeProfit":100
    },
    {
        "advisorId":"c4eb34e4-c9c3-4b7e-856d-d5d00588464d",
        "type":"CLOSE",
        "positionId":1000000000
    }
]
```

### Technical indicator data adapter.
For mapping data from origin request data sources to you're a technical indicator's library format you can use `Adapter` interface,
example of implementation you can see in `pro.laplace.adapter.ta4j.TAdapter.java`.


[webrequest]: https://www.mql5.com/en/docs/network/webrequest
[settings]:https://www.mql5.com/en/docs/network
[ta4j]:https://github.com/ta4j
[stategyExample]: https://github.com/triodjangopiter/mt4j/blob/master/src/main/java/pro/laplacelab/mt4j/example/Example.java