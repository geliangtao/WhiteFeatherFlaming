//获取应用实例
const app = getApp()
// pages/common/myinfo.js

Page({

  /**
   * 页面的初始数据
   */
  data: {
    wxlogin: true,
    balance: 100.00,
    freeze: 10000,
    score: 102,
    rechargeOpen: true,
    isopenacct: false,
    userInfo: {},
    hasUserInfo: false
  },

  getUserInfo: function (e) {
    let that =this;
    console.log(e)
    if (e.detail.userInfo){
      wx.login({
        success(res) {
          console.log(res);
          if (res.code) {
            //发起网络请求
            wx.request({
              url: 'http://10.7.20.157:9001/ifp',
              header: {
                'content-type': 'application/json' // 默认值
              },
              method: 'POST',
              data: {
                // code: res.code,
                // userinfo: e.detail.userInfo
                "SYS_HEAD": {
                  "BRANCH_ID": "01301",
                  "COMPANY": "BODC",
                  "DEST_BRANCH_NO": "990001",
                  "SEQ_NO": "1111111",
                  "SERVICE_CODE": "MBSD_IFP_UM",
                  "MESSAGE_TYPE": "1200",
                  "MESSAGE_CODE": "0001",
                  "USER_LANG": "CHINESE",
                  "SOURCE_BRANCH_NO": "0101",
                  "SOURCE_TYPE": "EN",
                  "TRAN_DATE": "20130127",
                  "TRAN_TIMESTAMP": "172023004",
                  "USER_ID": "CP0101",
                  "AUTH_FLAG": "N",
                  "AUTH_USER_ID": "CP0101",
                  "PROGRAM_ID": "EA101",
                  "TRAN_MODE": "ONLINE",
                  "APPR_USER_ID": "12",
                  "APPR_FLAG": "E",
                  "FILE_PATH": "12",
                  "PLATFORM_ID": "B00001",
                  "MAC_VALUE": "123"
                },
                "BODY": {
                  "THIRD_ID": "1",
                  "THIRD_OPENID": res.code.substr(0,5),
                  "THIRDUSERINFO": e.detail.userInfo,
                  "CLIENT_TIME": "21221",
                  "LOGIN_IP": "10.3.3.4"
                }
              },
              success: function (e) {
                console.log('开发者服务器回调:' + JSON.stringify(e));
                if(200 == e.statusCode){
                  wx.setStorageSync('token', e.data.token);
                  wx.setStorageSync('uid', e.data.uid);
                }else{
                  wx.showToast({
                    title: '登录失败！',
                  })
                }
              },
              fail: function(e) {
                console.log('开发者服务器调用失败:' + e.errMsg);
                wx.showToast({
                  title: '开发者服务失败',
                })
              }
            })
          } else {
            console.log('登录失败！' + res.errMsg);
            wx.showToast({
              title: '登录失败！' + res.errMsg,
            })
            this.setData({
              hasUserInfo: false
            })
            return;
          }
        }
      })
      app.globalData.userInfo = e.detail.userInfo
      this.setData({
        userInfo: e.detail.userInfo,
        hasUserInfo: true
    })
  }else {
      wx.showToast({
        title: '获取用户信息失败',
      })
      this.setData({
        hasUserInfo: false
      })
  }
},

  loginOut() {
    this.setData({
      hasUserInfo: false
    })
    wx.removeStorageSync('token');
    wx.removeStorageSync('uid');
    wx.reLaunch({
      url: 'pages/common/myinfo'
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})