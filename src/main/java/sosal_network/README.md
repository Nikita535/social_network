<h1 align="center">
  Information for users
</h1>


What is this project for?
-----------------------
This project was made solely for educational and educational purposes. The application is designed similar to the well-known social network - VK.

How you can use this?
---------
* The first step that you take when entering the service is registration, where you will need to enter basic information.

![image](https://user-images.githubusercontent.com/68530909/187542281-bafe8a11-741b-47e3-aaa3-81bb3b14c538.png)

![image](https://user-images.githubusercontent.com/68530909/187542432-c75779b7-e1c4-484b-9a70-94629491c917.png)

* The second stage of registration is filling in more specific data about you. Please pay close attention to the hints inside the input fields.

![image](https://user-images.githubusercontent.com/68530909/187542694-eded7d9d-7ea8-449a-870a-eb6069136cf6.png)

* After the second step, you will receive a message to the mail (which you indicated during registration) in order for you to confirm your mail (you need to follow the link in the letter). Please note that the verification code (it is hidden from your eyes) is valid for exactly 2 days! After the expiration of this code, you will need to resend the code to your mail.
* At this point, your registration is completed and you can use the functionality of the application, which consists of:
    * The ability to edit your profile data, including changing the avatar. (Please note that this information will be displayed on your profile).

  ![image](https://user-images.githubusercontent.com/68530909/187543222-c7d87fd4-a4bc-4bbf-bbba-29fd07eb2865.png)

    * You can add and remove posts (a post can be either empty or contain an unlimited number of images). Detailed information about the posts is on the "Posts" tab.

  ![image](https://user-images.githubusercontent.com/68530909/187543134-a723e1d2-fe65-4766-a51e-6de25c0a108d.png)

    * You can also add other users as friends, but your goal must accept your friend request, otherwise you will not be able to communicate. Detailed information about friends is on the "Friends" tab.

  ![image](https://user-images.githubusercontent.com/68530909/187543316-4a46b62c-6531-4fbe-bca8-7e5ee2f40c2b.png)

  * Well, where is the social network and buzz chat ... Of course you can do it !!! In the "messages" tab, you can choose the person with whom you want to chat. You may also receive notifications about user messages.

  ![image](https://user-images.githubusercontent.com/68530909/187543380-1c7bf90e-2cc3-4039-8c09-256e02b318d3.png)

-------------------------------------------------

<h1 align="center">
  Information for developers
</h1>

What languages were used?
------------------------
[![Top Langs](https://github-readme-stats.vercel.app/api/top-langs/?username=Nikita535&layout=compact)](https://github.com/Nikita535/social_network)

What libraries and dependencies did we use? (Since the application is built using the Gradle build system, all links will be provided to it.)
------
* ___Thymeleaf___ -  is a modern server-side Java template engine for both web and standalone environments.
    * ___spring-boot-starter-thymeleaf___
        * __link__ - https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf
    * ___thymeleaf-extras-springsecurity5___
        * __link__ - https://mvnrepository.com/artifact/org.thymeleaf.extras/thymeleaf-extras-springsecurity5
* ___Spring WEB___ - Starter for building web, including RESTful, applications using Spring MVC. Uses Tomcat as the default embedded container.
    * ___spring-boot-starter-actuator___
        * __link__ -  https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator
    * ___spring-boot-starter-data-jpa___ - Starter for using Spring Data JPA with Hibernate
        * __link__ - https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa
    * ___spring-boot-starter-validation___
        * __link__ - https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation
* ___PostgreSQL___ - PostgreSQL is a powerful, open source object-relational database.
    * __link__ - https://mvnrepository.com/artifact/org.postgresql/postgresql
* ___Security___ -  is a powerful and highly customizable authentication and access-control framework. It is the de-facto standard for securing Spring-based applications.
    * __link__ - https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security
* ___Email___ - Starter for using Java Mail and Spring Framework's email sending support.
    * __link__ - https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-mail
* ___LogBack___ - Implementation of the SLF4J API for Logback, a reliable, generic, fast and flexible logging framework.
    * __link__ - https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
* ___JSon___ - JSON is a light-weight, language independent, data interchange format. See http://www.JSON.org/ The files in this package implement JSON encoders/decoders in Java. It also includes the capability to convert between JSON and XML, HTTP headers, Cookies, and CDL. This is a reference implementation. There is a large number of JSON packages in Java. Perhaps someday the Java community will standardize on one. Until then, choose carefully.
    * __link__ - https://mvnrepository.com/artifact/org.json/json/20220320
* ___Websocket___ -  to build an interactive web application.
    * __link__ - https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-websocket
* ___AOP___ - AOP is a programming paradigm that aims to increase modularity by allowing the separation of cross-cutting concerns.
    * __link__ - https://mvnrepository.com/artifact/org.springframework/spring-aop

For a better understanding of the required dependencies, we advise you to go to the build.gradle file and look at the things used for yourself.

What makes our project special?
---------
* Imagine that you really love to capture every day of your life and share it with everyone by posting a post about it. At this rate, in a conditional year, quite a lot of posts will accumulate. From a performance point of view, it doesn't make sense to load everything at once, right? Therefore, we have implemented the functionality of the so-called infinite scroll. It lies in the fact that we load data by 3, depending on the degree of page scrolling: the further we scroll, the more it loads. You can read more here: https://www.jhipster.tech/tips/019_tip_infinite_scroll_with_slice.html
* Dynamic loading of most pages in the application. This technology allows you to display information without reloading the page. Works in combination with WebSocket and Ajax requests.

What are the launch requirements and rules?
-----
* If you have already been developing in Java, then just clone our repository and run it from your development environment, after changing the data in application.property.
* In case you have never worked with Java, here is a little tutorial that might help you - https://clck.ru/W3jNo

Cloud Service
-----
Пока пусто

*****The application is in the public domain and users can implement their ideas in it, thereby improving our creation!*****

* Developer accounts:
    * ___Nikita535___
    * ___HukoJlauII___
    * ___Renat21___