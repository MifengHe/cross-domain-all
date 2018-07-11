### cross-domain 跨域集成方案

#### 1 添加依赖

```html
<dependency>
    <groupId>com.os.cross-domain</groupId>
    <artifactId>cross-domain-lib</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 2 添加映射
##### 2.1 spring-boot
```java
import com.os.crossdomain.CrossDomainMainServlet;
import com.os.crossdomain.CrossDomainSubServlet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
class Application {
    
    @Bean
    public ServletRegistrationBean crossDomainMainJsServlet() {
        // 作为主页引用外部页面 需在主服务器添加该配置
        ServletRegistrationBean registration= new ServletRegistrationBean(new CrossDomainMainServlet());
        // 获取主引用JS
        registration.addUrlMappings("/cross-domain-main.js");
        // 引用页调用主页 加载此页面（每次重新加载）
        registration.addUrlMappings("/cross-domain-main.html");
        return registration;
    }
    
    @Bean
    public ServletRegistrationBean crossDomainSubJsServlet() {
        // 作为引用页面 需在引用服务器添加该配置
        ServletRegistrationBean registration= new ServletRegistrationBean(new CrossDomainSubServlet());
        // 引用页面添加引用JS
        registration.addUrlMappings("/cross-domain-sub.js");
        // 主页调用引用页 加载此页面（每次重新加载)
        registration.addUrlMappings("/cross-domain-sub.html");
        return registration;
    }  
}
 
   
```
##### 2.2 web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?> 
<web-app version="2.5" 
    xmlns="http://java.sun.com/xml/ns/javaee" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="
        http://java.sun.com/xml/ns/javaee 
        http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"> 
    
    <!--作为主页引用外部页面 需在主服务器添加该配置-->
    <servlet>
        <servlet-name>cross-domain-main</servlet-name>
        <servlet-class>com.os.crossdomain.CrossDomainMainServlet</servlet-class>
    </servlet>
    <!--获取主引用JS-->
    <servlet-mapping>
        <servlet-name>cross-domain-main</servlet-name>
        <url-pattern>/cross-domain-main.js</url-pattern>
    </servlet-mapping>
    <!--引用页调用主页 加载此页面（每次重新加载）-->
    <servlet-mapping>
        <servlet-name>cross-domain-main</servlet-name>
        <url-pattern>/cross-domain-main.html</url-pattern>
    </servlet-mapping>
    
    <!--作为引用页面 需在引用服务器添加该配置-->
    <servlet>
        <servlet-name>cross-domain-sub</servlet-name>
        <servlet-class>com.os.crossdomain.CrossDomainSubServlet</servlet-class>
    </servlet>
    <!--引用页面添加引用JS-->
    <servlet-mapping>
        <servlet-name>cross-domain-sub</servlet-name>
        <url-pattern>/cross-domain-sub.js</url-pattern>
    </servlet-mapping>
    <!--主页调用引用页 加载此页面（每次重新加载）-->
    <servlet-mapping>
        <servlet-name>cross-domain-main</servlet-name>
        <url-pattern>/cross-domain-sub.html</url-pattern>
    </servlet-mapping>
</web-app>
```

#### 3 demo

<pre>
    主服务：http://127.0.0.1:8080 
    引用服务：http://192.168.1.168:8081
    主页：  http://127.0.0.1:8080/main.html
    引用页：http://192.168.1.168:8081/sub.html
</pre>
 
```html
<!--主页(main.html)-->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Main App</title>
    <script type="text/javascript" src="http://127.0.0.1:8080/cross-domain-main.js"></script>
</head>
<body>
<h1>Main App</h1>
    <button onclick="sendRandom()">send Random</button>
    <iframe name="sub" src="http://192.168.1.168:8081/sub.html?url=http://http://127.0.0.1:8080:8080/cross-domain-main.html" style="border: none; width: 100%; height: 500px;"></iframe>
    <script type="text/javascript">
        // 创建对象CrossDomainMain
        var main = new CrossDomainMain('sub', 'http://192.168.1.168:8081/cross-domain-sub.html');

        function sendRandom() {
            main.dispatch("sendRandom", new Date().getTime(), function () {
                console.log(1, 'callback', arguments);
            });
        }

        main.action('popup', function(payload) {
           console.log('popup', payload);
        });
    </script>
</body>
</html>
```

```html
<!--引用页(sub.html)-->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sub App</title>
    <script type="text/javascript" src="http://192.168.1.168:8081/cross-domain-sub.js"></script>
</head>
<body>
<h1>Sub App</h1>
<button onclick="onPopup()">popup</button>
<script>
    var getParameter = function(name, url) {
        if (!url) url = window.location.href;
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    };
    var url = getParameter("url");
    var sub = new CrossDomainSub(url);

    sub.action('register', function () {
        console.log('register', arguments);
    });

    sub.action("sendRandom", function () {
        console.log('sendRandom', arguments);
        return 2222;
    });

    function onPopup() {
        sub.dispatch('popup', Math.random().toString(16).substr(2), function () {
            console.log('popup-cb', arguments);
        });
    }
</script>
</body>
</html>
```

#### 4 JS对象说明
```javascript
/**
*  跨域主页
*/
interface CrossDomainMain {
    
    /**
    * @param sub_name [string] required 引用iframe的name属性
    * @param url [string] 调用cross-domain-sub.html
    */
    constructor(sub_name, url);
    
    /**
    * @param action [string] 动作(监听什么动作)
    * @param onAction [function] 监听执行器
    *   
    *   @param onAction.payload [object] 携带参数
    *       onAction.payload.action [string] 动作
    *       onAction.payload.data [any] 值
    *   @return [any] 返回值作为dispatch.callback入参
    *   onAction = function (payload) {}
    *   
    */
    action(action, onAction);
    
    /**
     *  与action 一样
     *  唯一区别，只能执行一次(执行后，就销毁）
     */
    once(aciton, onAction);
    
    /**
    * @param action [string] 动作
    * @param callback [function] 回调函数
    * @param onAction.payload [object] 携带参数
    *       onAction.payload.action [string] 动作
    *       onAction.payload.data [any] 值
    *   calllback = function(payload){}
    */
    dispatch(aciton, callback);
}

/**
*  跨域引用页
*/
interface CrossDomainSub {
    /**
    * @param url [string] 调用cross-domain-sub.html
    */
    constructor(url);
    
    /**
    * @param action [string] 动作(监听什么动作)
    * @param onAction [function] 监听执行器
    *   
    *   @param onAction.payload [object] 携带参数
    *       onAction.payload.action [string] 动作
    *       onAction.payload.data [any] 值
    *   @return [any] 返回值作为dispatch.callback入参
    *   onAction = function (payload) {}
    *   
    */
    action(action, onAction);
    
    /**
     *  与action 一样
     *  唯一区别，只能执行一次(执行后，就销毁）
     */
    once(aciton, onAction);
    
    /**
    * @param action [string] 动作
    * @param callback [function] 回调函数
    * @param onAction.payload [object] 携带参数
    *       onAction.payload.action [string] 动作
    *       onAction.payload.data [any] 值
    *   calllback = function(payload){}
    */
    dispatch(aciton, callback);
}

```