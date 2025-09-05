# 验证码控件
1. 支持2种边框
2. 支持选中修改
3. 支持输入长度检测和完成键双回调

![portrait.gif](/gif.gif)

### 导入 ###
~~~gradle
    repositories {
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        implementation 'com.github.ray-tianfeng:captchaedittext:v1.0.0'
    }
~~~

### 属性 ###

<table>
 <tr>
  <td>属性名称</td>
  <td>类型</td>
  <td>说明</td>
 </tr>
 <tr>
  <td>borderType</td>
  <td>enum</td>
  <td>rectangle:正方形方块边框  underline：下滑横线边框</td>
 </tr>
 <tr>
  <td>borderSize</td>
  <td>dimension</td>
  <td>一个验证码字符所占的大小</td>
 </tr>
 <tr>
  <td>borderStrokeWidth</td>
  <td>dimension</td>
  <td>验证码边框线的宽度</td>
 </tr>
 <tr>
  <td>captchaLength</td>
  <td>integer</td>
  <td>验证码长度</td>
 </tr>
 <tr>
  <td>intervalPadding</td>
  <td>dimension</td>
  <td>每个验证码之间的间隔</td>
 </tr>
 <tr>
  <td>radius</td>
  <td>dimension</td>
  <td>正方形边框时圆角度数，如果为borderSize的一半时，为圆形边框。下滑横线时设置无用</td>
 </tr>
 <tr>
  <td>norBorderColor</td>
  <td>color</td>
  <td>正常时边框或者下滑横线的颜色</td>
 </tr>
 <tr>
  <td>focusBorderColor</td>
  <td>color</td>
  <td>获得输入焦点时边框或者下滑横线的颜色</td>
 </tr>
 <tr>
  <td>callbackAuto</td>
  <td>boolean</td>
  <td>是否自动回调。当输入长度等于captchaLength时回调</td>
 </tr>
 </table>

### 回调 ###
1. 当输入当度等于captchaLength时回调  
2. 当输入用户按下完成按键时回调
~~~Koltin
findViewById<CaptchaEditText>(R.id.id).setCallback {
        code ->
    Toast.makeText(MainActivity@this, "验证码：$code", Toast.LENGTH_SHORT).show()
}
~~~