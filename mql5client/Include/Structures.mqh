#property copyright "Copyright © 2006-2017"
#property version "1.12"
#property strict

struct HttpResponse
  {
   int               status;
   string            body;
  };

struct HttpRequest
  {
   string            url;
   string            body;
   string            headers;
  };

struct RestConfig
  {
   string            host;
   int               port;
   string            headers;
   int               timeout;
  };

enum SignalType { BUY = "BUY", SELL = "SELL", UPDATE = "UPDATE", CLOSE = "CLOSE", NO_ACTION = "NO_ACTION" };
struct Signal
  {
   long       positionId;
   string     advisorId;
   SignalType type;
   double     lot;
   int        stopLoss;
   int        takeProfit;
  };

struct Position
  {
   bool              isHistory;
   string            type;
   long              magic;
   long              advisorId;
   long              positionId;
   double            lot;
   int               stopLoss;
   int               takeProfit;
   long              openAt;
   long              closeAt;
   double            profit;
  };