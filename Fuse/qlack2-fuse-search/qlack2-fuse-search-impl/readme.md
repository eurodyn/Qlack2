# ES tips

- You can browse the content in our ES instance by installing a plugin. Go to 
the ```bin``` folder of ES and type:  
 ```./plugin -install mobz/elasticsearch-head```  
The interface will become available under:  
http://localhost:9200/_plugin/head/

- To be able to index binary content (e.g. MS Word, PDFs, etc.) you need yet another plugin. 
Go to the ```bin``` folder of ES and type:  
 ```./plugin -install elasticsearch/elasticsearch-mapper-attachments/2.7.1```
