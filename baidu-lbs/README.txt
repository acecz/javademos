请修改
src/main/resources/webapp.properties
中baidu.ak为自己的ak

使用POST json调用接口
ip:port/{appname}/rest/test/findPath
body
[
  {
    "name": "易朗国际教育",
    "lat": 40.21666,
    "lng": 116.237639,
    "region": "北京"
  },
  {
    "name": "荣华中路20",
    "lat": 39.79535,
    "lng": 116.508817,
    "region": "北京"
  }
]
做测试