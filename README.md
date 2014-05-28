oa4mp-shibboleth
================

Version 1.1.1-nesi.1

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


# Installation:

Due to the way that oa4mp-shibboleth extends 'OAuth for MyProxy', it has to be
built and deployed as its own .war file. If this is done with maven 
(`mvn clean install`), it will automatically download and include all the parts 
it requires from 'OAuth for MyProxy'. 

The installation requirements and instructions are identical to the ones listed 
in [3], and will not be replicated here.

In addition to this, on the host running MyProxy, all the components required by
the application that is used to build the certificate contents have to be 
installed (see below).


# Configuration:

Again, the generic instructions listed on [3] apply.

## Host running the oa4mp webapp in Tomcat:

In addition to this, the following configuration specific to oa4mp-shibboleth 
has to be added to the server configuration file [4]:

- in the `<authorizationServlet>` element, set the attribute `useHeader="false"`

- below the `<service>` element, add the following custom element:

````
  <shibbolethUsernameTransformer
    attributesToSend="[attributes]"
    myproxyWorkaround="true"
    requireHeader="true"
    processDuringStartAction="true" 
    returnDnAsUsername="true" />

<!--
  attributesToSend:         CSV list of request attributes to be sent as to 
                            MyProxy as username (CSV encoded).
  myproxyWorkaround:        Prepend a "/" to the username sent to MyProxy, to 
                            get around MyProxy's limitation in username length
  requireHeader:            Require at least one attribute to be present. This 
                            will cause an exception to be thrown if none of 
                            the configured attributes can be found. This check 
                            works in addition to the checking that is done on 
                            the MyProxy host while the DN / extensions for the 
                            certificate are constructed
  processDuringStartAction: Process during 'start' action, i.e. before the 
                            approval page is displayed to the user, instead of 
                            after approval and before MyProxy invocation
  returnDnAsUsername        Use the first certificate's DN as the username 
                            that is returned to the OAuth client (formatted in 
                            'Globus' format, i.e. with '/' as separator)
-->
````

 
## Host running MyProxy    

The actual integration of attributes supplied to 'OAuth for MyProxy' into 
distinguished name and extensions for the MyProxy certificate is done
on the host that runs MyProxy. As this is a process that is specific to every
installation, only an example of how this can be done is included, in the file
`doc/myproxy-mapapp.pl`. If this example has to be used, then perl and all perl 
modules used in the example application have to be installed on this host.

When the oa4mp webapp calls MyProxy, it supplies the attributes retrieved from 
the web session as comma separated values (CSV) packed into the string that is 
designated as 'username' in the  MyProxy protocol.
MyProxy has hooks to call external applications to 'map' the 'username' to an 
appropriate distinguished name (DN) and appropriate extensions for the 
certificate to be issued. These apps can be used to extract the attribute values
from the CSV string in 'username' and build the desired DN and attributes. If the
`myproxyWorkaround` switch has been set in the webapp's configuration, these scripts
also have to take care of removing the leading `/` that is added 'username'.

These scripts can be configured by the following settings in `myproxy.conf`:

````
certificate_mapapp "[path to file]/myproxy-mapapp.pl"
certificate_extapp "[path to file]/myproxy-extapp.pl"
````

The example file supplied in `doc/myproxy-mapapp.pl` is designed to be used as both 
these applications, depending on the name it is called under.

Another function of these applications is to do sanity / validity checking on the
attributes supplied to them: If all the attributes required to issue are present
and valid, then the applications are expected to print the DN or extensions on the
standard output and return 0.
If a certificate should not be issued with the attributes supplied, then the
applications can signal that by returning a non-zero value. This will cause the call
to MyProxy to fail, and the oa4mp webapp to return an error page to the user.


[1] http://grid.ncsa.illinois.edu/myproxy/oauth/server/index.xhtml

[2] https://shibboleth.net/products/service-provider.html

[3] http://grid.ncsa.illinois.edu/myproxy/oauth/server/index.xhtml

[4] http://grid.ncsa.illinois.edu/myproxy/oauth/server/configuration/server-configuration-file.xhtml
