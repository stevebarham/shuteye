Shuteye
=======
**Like JdbcTemplate, but for HTTP, with no runtime dependencies**

Shuteye is a library to build RESTful API clients. It has no dependencies other than the JRE. It implements
[RFC 6570](https://tools.ietf.org/html/rfc6570) for URI template construction, and provides a fluent API to
execute HTTP requests.

No dependency tree hell, no version conflicts, no spooky side effects. Just clean, fast, simple code.

[![Build Status](https://travis-ci.org/stevebarham/shuteye.svg?branch=master)](https://travis-ci.org/stevebarham/shuteye)

Installation
------------
Add the following Maven dependency to use Shuteye:

    <dependency>
        <groupId>net.ethx.shuteye</groupId>
        <artifactId>shuteye</artifactId>
        <version>0.3.0</version>
    </dependency>

Tests
-----
Shuteye has excellent code coverage. URI templates are tested via the [uritemplate-test](https://github.com/uri-templates/uritemplate-test)
test project. HTTP template testing uses [httpbin](http://httpbin.org).

To run the tests, check out the project, and run the Maven test goal:

    mvn clean test

Contributors
------------
Steve Barham (<shuteye@ethx.net>)

License
-------
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
