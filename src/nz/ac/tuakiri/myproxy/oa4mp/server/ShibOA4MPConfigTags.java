package nz.ac.tuakiri.myproxy.oa4mp.server;

public class ShibOA4MPConfigTags {

	public static final String SHIBBOLETH_USERNAME_TRANSFORMER = "shibbolethUsernameTransformer";

	public static final String SHIBBOLETH_USERNAME_TRANSFORMER_ATTRIBUTES_TO_SEND = "attributesToSend"; // CSV list of request attributes to be sent as to MyProxy as username (CSV encoded).
	public static final String SHIBBOLETH_USERNAME_TRANSFORMER_MYPROXY_WORKAROUND = "myproxyWorkaround"; // Prepend a "/" to the username sent to MyProxy, to get around MyProxy's limitation in username length
    public static final String SHIBBOLETH_USERNAME_TRANSFORMER_REQUIRE_HEADER = "requireHeader"; // Require at least one attribute to be present. This will cause an exception to be thrown if none of the configured attributes can be found.
	public static final String SHIBBOLETH_USERNAME_TRANSFORMER_PROCESS_DURING_START = "processDuringStartAction"; // Process during 'start' action, i.e. before the approval page is displayed to the user, instead of after approval and before MyProxy invocation
	public static final String SHIBBOLETH_USERNAME_TRANSFORMER_RETURN_DN_AS_USERNAME = "returnDnAsUsername"; // Use the first certificate's DN as the username that is returned to the OAuth client (formatted according to RFC1779)
}
