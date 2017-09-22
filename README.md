# Geode-Custom-Expiration

A configuration based custom region and entry expiration supporting expiration of a region's PDX objects not covered by the basic GemFire expiration policies.

The custom region expiration supports the following expiration types:

  **time-to-live (TTL)**
  - region-time-to-live 
  - entry-time-to-live
  
  **idle time (TTI)**
  - region-idle-time 
  - entry-idle-time

To support region expiration the following XML statement must be added to the cluster configuration XML or cache XML:

<region>
  <region-attributes statistics-enabled="true">
    <entry-idle-time>
      <expiration-attributes timeout="60" action="local-invalidate">
        <custom-expiry>
          <class-name>utils.geode.server.custom.expiration.CustomExpiration</class-name>
        </custom-expiry>
      </expiration-attributes>
    </entry-idle-time>
  </region-attributes>
</region>

The expiration-attributes tag above needs to be in the configuration but is not used. The values for the timeout and action are specified by passing the following property for each region where custom expiration is needed in the server start command.

--J-D${region-name}ExpirationFields=fieldName,fieldValue,timeToExpire;fieldName,fieldValue,timeToExpire;...;...;

where $(region-name} is the region name where the custom expiration will be performed.

Format of the ${region-name}ExpirationFields java property:

A field block is denoted by a semi-colon and multiple blocks are supported. 

Each field block contains four (4) parts separated by a comma. 
  Part 1: The field name in the object that will be used to compare for expiration. 
  Part 2: The value that will be used to compare the object's field value to. Currently only an equal condition is supported. The value             is converted into the object field name's data type and compared. 
  Part 3: The time-to-live or idle-time in seconds.  
  Part 4: The action to be performed. Valid actions are DESTROY. INVALIDATE, LOCAL-DESTROY and LOCAL-INVALIDATE
