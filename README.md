Shuteye
=======
Shuteye is a library to build RESTful API clients. It has no dependencies other than the JRE. It implements
[RFC 6570](https://tools.ietf.org/html/rfc6570) for URI template construction, and provides a fluent API to
execute HTTP requests.

[![Build Status](https://travis-ci.org/stevebarham/shuteye.svg?branch=master)](https://travis-ci.org/stevebarham/shuteye)

Motivation
----------
Shuteye's design goal is to provide a library which:

* Is self-contained
* Can be safely shaded, to provide isolation of client access code

Many libraries exist which can be used to build REST clients in Java. Unfortunately, most of these libraries have
characteristics which make them unsuitable for building client access libraries.

* Require additional dependencies
* Cannot be included or used in isolation

Other libraries pull in large trees of dependencies; there is nothing inherently wrong with code reuse, but
when building a client access library, every additional dependency adds both size and complexity.

For example, if you decide to use RESTEasy or Jersey to build a client access library, each will attempt to register
itself as the de-facto implementation of JAX-RS in the executing VM. If another client access library is using a
 different implementation, you will likely encounter issues.

If you decide to use Unirest, you have committed to bringing in specific versions of httpclient, httpasyncclient,
httpmime, and json - plus all of their transitive dependencies.

### Shuteye is different
Shuteye only depends on classes provided by the JRE. There are no external dependencies.

When using Shuteye, you are encouraged (though not required) to shade and relocate the Shuteye classes into your
own library, via the maven-shade-plugin. This removes the chance of Shuteye conflicting with any other code in your
library - even if a different version of Shuteye is used elsewhere by another library.

Consumers of Shuteye are not forced to use any additional dependencies, or pinned to a particular version of a
transitive dependency.

No dependency tree hell, no version conflicts, no spooky side effects. Just clean, fast, simple code.

Features
--------

### URI Templates

1. Shuteye implements RFC 6570 to level-4
1. Shuteye passes all tests in [uritemplate-test](https://github.com/uri-templates/uritemplate-test)
1. Shuteye supports compilation of templates for efficient reuse

### HTTP Requests

1. Fluent API for constructing requests
1. Support for form-style POST requests
1. Support for raw (entity-based) POST requests
1. Simple extension via ResponseTransformer to extract responses
1. Support for GZIP compressed responses

Code Example
------------

### Constructing a URI template

Templates can be parsed and processed in a single step, by passing the variables alongside the template:

    Map<String, Object> vars = new HashMap<>();
    vars.put("user", "stevebarham");
    vars.put("repo", "jcodemodel");
    vars.put("function", "commits");

    String uri = UriTemplate.process("https://api.github.com/repos{/user,repo,function,id}", vars)

This produces the following URI:

    https://api.github.com/repos/stevebarham/jcodemodel/commits

### Compiling a URI template, and processing it with different variables

When a template will be used multiple times, it's probably more efficient to compile it in advance, then just
process it with different variables.

    UriTemplate template = UriTemplate.parse("https://api.github.com/repos{/user,repo,function,id}");

    Map<String, Object> vars = new HashMap<>();
    vars.put("user", "stevebarham");
    vars.put("repo", "jcodemodel");
    vars.put("function", "commits");
    String uri1 = template.process(vars);

    vars.put("user", "phax");
    String uri2 = template.process(vars);

This produces the following URIs:

    https://api.github.com/repos/stevebarham/jcodemodel/commits
    https://api.github.com/repos/phax/jcodemodel/commits

### Making an HTTP request
todo: document. See ShuteyeTest for the moment.

Installation
------------
todo - Maven central, standard dependency fragment

API Reference
-------------
todo - write JavaDoc, publish, link

Tests
-----
Shuteye has excellent code coverage. URI templates are tested via the [uritemplate-test](https://github.com/uri-templates/uritemplate-test)
test project. HTTP client testing uses [httpbin](http://httpbin.org).

To run the tests, check out the project, and run the Maven test goal:

    mvn clean test

Contributors
------------
Steve Barham (<shuteye@ethx.net>)

License
-------
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
