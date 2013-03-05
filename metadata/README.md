# Summary

* apis.csv: metadata about APIs in the Expapus-specific Qualitas corpus
* subapis.csv: basic metadata about sub-APIs (as sets of API members).
* subapis/*.csv: actual quantification of sub-API members.

"apis.csv" is essentially complete.

The sub-API-related files provide only a few examples. 

# Metamodel of apis.csv

Columns of the CSV file:
* Id (mandatory): a short name to be used in reports etc.
* Name (optional): a (possibly longer) assigned API name
* Prefix (mandatory): a package prefix for the API
* Subpackages (mandatory): a 0/1 encoded bit for inclusion of subpackages

"Name", if present, binds to "http://101companies.org/wiki/Technology:Name". "Name" may be missing, if available API documentation does not suggest any obvious contender for the name, in which case the package prefix is counted as name. If "Name" is not present, then "Prefix" binds to "http://101companies.org/wiki/Java:Preffix". 

# Metamodel of subapis.csv

Columns of the CSV file:
* Id (mandatory): a short name to be used in reports etc.
* Name (required): an assigned API name
* Package (mandatory): a package in which to quantify members.

The actual members are quantified in the associated file in the subapis folder.
