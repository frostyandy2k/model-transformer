Model Converter

Inspired by and using emftriple(https://github.com/ghillairet/emftriple): a small utility that can transform models to other formats.

For example: 
Ecore (XMI) to TTL RDF

```Usage: java -jar metaModelURI inputModelURI outputModelURI```

```Example: java -jar mm.ecore in/model.xmi out/outputmodel.ttl```

An attempt will be made to resolve the paths platform independent. If it does not work retry with fully specified absolute paths according to the used platform

Feel free to use, modify, share, suggest.

MIT License
