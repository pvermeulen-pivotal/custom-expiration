# Geode-Custom-Expiration

A configuration based custom region and entry expiration that supports expiration of a region's PDX objects (only PDX object supported)  based on the contents of a field and its value in the PDX object.

The custom region expiration supports the following expiration types:

  **time-to-live (TTL)**
  - region-time-to-live 
  - entry-time-to-live
  
  **idle time (TTI)**
  - region-idle-time 
  - entry-idle-time

To support region expiration the following XML statement must be added to each region where custom exiration is needed in the cluster configuration XML or cache XML:

**idle time (TTI)**

\<region name="region" refid="partition-persisted-redundant" \>
  \<region-attributes statistics-enabled="true"\>
    \<entry-idle-time\>
      \<expiration-attributes timeout="60" action="local-invalidate">
        \<custom-expiry>
          \<class-name>utils.geode.server.custom.expiration.CustomExpiration</class-name>
        \</custom-expiry>
      \</expiration-attributes>
    \</entry-idle-time>
  \</region-attributes>
\</region>

**time-to-live (TTL)**

\<region name="region" refid="partition-persisted-redundant" >\
  \<region-attributes statistics-enabled="true">\
    \<entry-time-to-live>\
      \<expiration-attributes timeout="60" action="local-invalidate">\
        \<custom-expiry>\
          \<class-name>utils.geode.server.custom.expiration.CustomExpiration</class-name>\
        \</custom-expiry>\
      \</expiration-attributes>\
    \</entry-time-to-live>\
  \</region-attributes>\
\</region>\


The expiration-attributes XML tag above needs to be in the configuration for compliance but is not used. The timeout value and action are specified by passing the a property for each region where custom expiration is needed in the server start command.

**GFSH**
start server --bind-address=localhost --dir=c:\temp\gemfire\server1 --locators=localhost[10000] --cache-xml-file=c:\temp\gemfir
e\cache.xml --name=server1 --use-cluster-configuration=false --J=-Dgemfire.log-file=server1.log --J=-Dgemfire.statistic-archive-fi
le=server1.gfs --include-system-classpath=true --server-port=10100 
--J-D${region-name}ExpirationFields=fieldName,fieldValue,timeToExpire,action;fieldName,fieldValue,timeToExpire,action;...;...;

where *$(region-name}* is the region name where the custom expiration will be performed.

To support multiple regions add a different property for each region.

Format of the ${region-name}ExpirationFields java property is as follows:

   A field block is denoted by a semi-colon and multiple blocks are supported. 

   Each field block contains four (4) parts separated by a comma. 
      Part 1: The field name in the object that will be used to compare for expiration. 
      Part 2: The value that will be used to compare the object's field value to. Currently only an equal condition is supported. The                   value is converted into the object field name's data type and compared. 
      Part 3: The time-to-live or idle-time in seconds.  
      Part 4: The action to be performed. Valid actions are DESTROY. INVALIDATE, LOCAL-DESTROY and LOCAL-INVALIDATE
