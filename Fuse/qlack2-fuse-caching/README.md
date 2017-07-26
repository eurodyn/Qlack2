# Caching configuration

```
# Activation of cache.
active=true

# The maximum number of entries in the cache. Leave at '0' for an unbound cache.
maxEntries=0

# The amount of time (in msec) an entry remains in the cache. Leave at '0' for an unlimited amount
# of time (note that a specific cache's implementation may have additional eviction strategies that
# may eventually remove entries irrespectively of this value).
expiryTime=0

# Cache implementation-specific entries to be passed to the cache client service.
cacheURL=
```

```
config:edit com.eurodyn.qlack2.fuse.caching
config:property-set active true
config:property-set maxEntries 0
config:property-set expiryTime 1800000
config:property-set cacheURL
config:update
```