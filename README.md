
<h2 align="center">Welcome to SQLOJ 👋</h2>

<img alt="SQLOJ" src="doc/images/logo.png"/>
<p align="center">
  <img alt="Version" src="https://img.shields.io/badge/version-0.1.0-blue.svg?cacheSeconds=2592000" />
  <a href="https://blog.csdn.net/weixin_43090100/article/details/118505354" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" />
  </a>
  <a href="https://opensource.org/licenses/MIT" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg" />
  </a>
</p>

> A lightweight database system experimental platform that integrates an OJ of SQL and a variety of practical functionalities.


### ✨ [Demo](http://101.34.91.143:8080/)

## Install

```sh
docker pull ptyin/sqloj
```

## Usage

```sh
docker run -d -p 80:80 -v <mongo-db-path>:/data/db -v <mongo-configdb-path>:/data/configdb -v <sqlite-path>:/var/lib/sqloj ptyin/sqloj:latest 
```

where \<mongo-db-path\>\<mongo-configdb-path\>\<sqlite-path\> denotes the data persistence directories path in your server. e.g.,

In Windows:

```shell
docker run -d -p 80:80 -v D:\SQLOJ\mongodb\data:/data/db -v D:\SQLOJ\mongodb\config:/data/configdb -v D:\SQLOJ\sqlite:/var/lib/sqloj --name test ptyin/sqloj:latest 
```

In Linux:

```shell
docker run -d -p 80:80 -v /opt/SQLOJ/mongodb/data:/data/db -v /opt/SQLOJ/mongodb/config:/data/configdb -v /opt/SQLOJ/sqlite:/var/lib/sqloj --name test ptyin/sqloj:latest 
```


## Run tests

```sh
npm run test
```

## Functionality

### Module Design

![modules](doc/images/modules.png)

### User Management

#### User Role

- teacher
  - Default teacher username: admin
  - Default teacher password: tsxt-adm1n
- student

#### Login Panel

![login](doc/images/login.png)

### Teacher Example Page

#### Publish an assignment

![addAssignment](doc/images/addAssignment.png)

#### Add questions in a certain assignment

![addQuestion](doc/images/addQuestion.png)


#### Upload a database

![addDatabase](doc/images/addDatabase.png)

### Student Example Page

#### View question list

![questions](doc/images/questions.png)


#### Review uploaded records

![records](doc/images/records.png)

Status might be (RUNNING/AC/WA/RE/TLE)。

#### Check specific information in a record

![recordDetail](doc/images/recordDetail.png)

## Author

👤 **Peter Yin**

* Website: http://ptyin.asia/
* Github: [@PTYin](https://github.com/PTYin)

## Show your support

Give a ⭐️ if this project helped you!

## 📝 License

Copyright © 2021 [Peter Yin](https://github.com/PTYin).<br />
This project is [MIT](https://opensource.org/licenses/MIT) licensed.
