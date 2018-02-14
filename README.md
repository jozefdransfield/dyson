***Example Responses***


**REQUEST-PRODUCT-ENVIRONMENT-CURRENT-SENSOR-DATA**

`
{"msg":"ENVIRONMENTAL-CURRENT-SENSOR-DATA","time":"2018-02-09T23:17:15.000Z","data":{"tact":"2944","hact":"0053","pact":"0001","vact":"0000","sltm":"OFF"}}
`
HACT --> Humidity
VACT --> Volatile Compounds
TACT --> Temperature
PACT --> DUST
sltm --> SLeep timer

**REQUEST-CURRENT-STATE**

`
{"msg":"CURRENT-STATE","time":"2018-02-09T23:29:58.000Z","mode-reason":"PUI","state-reason":"ENV","dial":"OFF","rssi":"-45","product-state":{"fmod":"AUTO","fnst":"OFF","fnsp":"AUTO","qtar":"0003","oson":"OFF","rhtm":"ON","filf":"4119","ercd":"NONE","nmod":"OFF","wacd":"NONE"},"scheduler":{"srsc":"f7ae","dstv":"0001","tzid":"0001"}}
`

fmod --> Fan Mode
fnsp --> Fan Speed
oson --> Fan Oscilation