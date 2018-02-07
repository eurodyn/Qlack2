# Patch for TwelveMonkeys SVG reader

This is to patch `imageio-batik` to remove a classloader lookup before
registering the reader, so that it works in OSGi. See
[relevant discussion](https://github.com/haraldk/TwelveMonkeys/issues/405).

The change consists of patching `com.twelvemonkeys.imageio.plugins.svg.SVGProviderInfo`
as described above.

## When this patch should be removed
* A new version of [imageio-batik](https://github.com/haraldk/TwelveMonkeys/tree/master/imageio/imageio-batik)
 containing the above patch is released.

## Version checks
Here are the versions we have already checked to be non-conformant
(e.g. they do not contain our patch):

* TwelveMonkeys 3.3.2