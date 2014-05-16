oa4mp-shibboleth
================

Version 1.1.1

Michael Keller <michael.keller@canterbury.ac.nz>, 14/05/2014


oa4mp-shibboleth is an extension to the 'OAuth for MyProxy' released in [1].
'OAuth for MyProxy' is a delegation service for MyProxy. It allows clients 
to request certificates and users to securely authorize them using the 
OAuth delegation protocol.

oa4mp-shibboleth defines an extension to 'OAuth for MyProxy' that allows 
user information to be retrieved from the environment that the OAuth server 
is running in. This information can then to be added to the certificates 
issued by MyProxy, either as part of the distinguished name (DN), or as 
extensions of the X509 certificate. 

oa4mp-shibboleth was originally developed to enable retrieval and processing 
of user information that is supplied by a shibboleth service provider [2] 
running on the same host as the OAuth server. But as it is fully configurable, 
it can be adapted to retrieve and process user information that is supplied 
to the tomcat environment that the OAuth servlet is running in by any other means.


Installation:
=============

Due to the way that oa4mp-shibboleth extends 'OAuth for MyProxy', it has to be
built and deployed as its own .war file. If this is done with maven 
('mvn clean install'), it will automatically download and include all the parts 
it requires from 'OAuth for MyProxy'. 

The installation requirements and instructions are identical to the ones listed 
in [3], and will not be replicated here.

In addition to this, on the host running MyProxy, perl has to be installed, 
plus all the perl modules loaded in the included file 'myproxy-mapapp.pl'.


Configuration:
==============

Again, the generic instructions listed on [3] apply.

In addition to this, the following configuration specific to oa4mp-shibboleth 
has to be added to the server configuration file:

- in the <authorizationServlet> element, set the attribute useHeader="false"

- if the user name returned by OAuth should be human readable, in the 
  <authorizationServlet> element, set the attribute returnDnAsUsername="true"
  
- below the <service> element, add the following custom element:

  <shibbolethUsernameTransformer
    attributesToSend="[attributes]"
    myproxyWorkaround="true"
    requireHeader="true"
    processDuringStartAction="true" />
    
  attributesToSend:         CSV list of request attributes to be sent as to 
                            MyProxy as username (CSV encoded).
  myproxyWorkaround:        Prepend a "/" to the username sent to MyProxy, to 
                            get around MyProxy's limitation in username length
  requireHeader:            Require at least one attribute to be present. This 
                            will cause an exception to be thrown if none of the
                            configured attributes can be found.
  processDuringStartAction: Process during 'start' action, i.e. before the 
                            approval page is displayed to the user, instead of 
                            after approval and before MyProxy invocation

    
On the host running MyProxy, add the included file 'myproxy-mapapp.pl'. 
Configure it to be used to 'map' usernames to DNs in the MyProxy config:

certificate_mapapp "[path to file]/myproxy-mapapp.pl"

If attribute values should be encoded as certificate extensions as well, 
create a symlink called 'myproxy-extapp.pl' pointing to 'myproxy-mapapp.pl', 
and add the following to the MyProxy config:

certificate_extapp "/opt/myproxy-ca/myproxy-extapp.pl"


[1] http://grid.ncsa.illinois.edu/myproxy/oauth/server/index.xhtml
[2] https://shibboleth.net/products/service-provider.html
[3] http://grid.ncsa.illinois.edu/myproxy/oauth/server/index.xhtml
