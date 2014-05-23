#!/usr/bin/perl -T -w -I /opt/myproxy-ca

=head1 myproxy-mapapp.pl

mapapp / extapp script for myproxy to build DN / certificate extensions from 
attributes passed by oa4mp-shibboleth as username.
oa4mp-shibboleth allows request attributes to be submitted as username in CSV format.
For example, these attributes could come from an authentication
that the user performed with shibboleth.

This script can be invoked with two different names:

myproxy-mapapp.pl: print DN for certificate
myproxy-extapp.pl: print extensions for certificate

MyProxy and this script handle only OpenSSL-formated DNs - e.g.:
/C=OS/O=Organization/CN=My Common Name

=head2 Usage

myproxy-mapapp.pl [<username>]
myproxy-extapp.pl [<username>]

=head2 Return Value

Zero on success, printing DN / certificate extensions to STDOUT; one on error.

=head2 Version

version 1.1.1

=cut

######################################################################
#
# My DN namespace prefix

my $namespace = "/DC=[country code]/DC=[rest of namespace prefix]";


######################################################################
#
# 

use Sys::Syslog;
use File::Basename;
use Text::CSV;
use Switch;
use Email::Valid;

######################################################################
#
# Set up logging

my $runas = basename($0);
openlog($runas, "pid", "auth");

######################################################################
#
# Get requested DN and validate

my $input = $ARGV[0];

syslog("info", "input: \"%s\", running as \"%s\"", $input, $runas);

if (!defined($input) || ($input eq ""))
{
    syslog("err", "Missing argument");
    exit(1);
}

# Remove the "/" added by "myproxyWorkaround=true" on the frontend to take care of
# the username length restriction in myproxy
if ($input =~ m/^\/(.*)$/) {
  $input = $1
} else {
    syslog("err", "Invalid argument format: \"%s\"", $input);
    exit(1);
}

my $csv = Text::CSV->new();
if (!$csv->parse($input))
{
    syslog("err", "Error (%s) parsing input CSV: \"%s\"", "" . $csv->error_diag(), $input);
    exit(1);
}

######################################################################
#
# Extract attributes

my %fields = $csv->fields();
my $attribute;
my $value;
while (($attribute, $value) = each(%fields)) {
  syslog("info", "Attribute: \"%s\", value \"%s\"", $attribute, $value);
}

######################################################################
#
# Generate output

my $result;
switch ($runas) {
  case "myproxy-mapapp.pl" {
    # Check that all required attributes were found in the username
    if (!defined($fields{"organisation"})
      || !defined($fields{"commonName"})
      || !defined($fields{"sharedToken"})) {
      syslog("err", "Error: Required field for DN missing: \"%s\"", $input);
      exit(1);
    }

    # Build DN from attributes
    $result = $namespace . "/O=" . $fields{"organisation"} . "/CN=" . $fields{"commonName"} . " " . $fields{"sharedToken"} . "\n";

    syslog("info", "DN: \"%s\"", $result);
  }
  case "myproxy-extapp.pl" {
    # Build multiline string containing all extensions (one per line)
    # to be added to the certificate
    if (defined($fields{"mail"})) {
        $result = $result . "subjectAltName=email:" . $fields{"mail"} . "\n";
    }
    if (defined($fields{"assurance"})) { # assertion level
        $result = $result . "1.3.6.1.4.1.5923.1.1.1.11=ASN1:UTF8String:" . $fields{"assurance"} . "\n";
    }
    if (defined($fields{"affiliation"})) { # unscoped affiliation
        $result = $result . "1.3.6.1.4.1.5923.1.1.1.1=ASN1:UTF8String:" . $fields{"affiliation"} . "\n";
    }
    if (defined($fields{"sharedToken"})) {
        $result = $result . "1.3.6.1.4.1.27856.1.2.5=ASN1:UTF8String:" . $fields{"sharedToken"} . "\n";
    }
    if (defined($fields{"principalName"})) { # eppn
        $result = $result . "1.3.6.1.4.1.5923.1.1.1.6=ASN1:UTF8String:" . $fields{"principalName"} . "\n";
    }

    syslog("info", "extensions: \"%s\"", $result);
  }
}

# All seems well, print out result so MyProxy will pick it up and return
# success.

print $result;
exit(0);

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################

