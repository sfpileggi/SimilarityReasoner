# SimilarityReasoner

This reasoner, implemented in Java, is built upon _PELLET_ (OWL-DL reasoner) to reason on similarities. The provided API consists of a SPARQL interface which allows accessing the reasoner's functionalities pervasively.

This API uses the following libraries:
* antirall-2.7.6.jar
* arq-2.8.7.jar
* aterm-java-1.8.2.jar
* com.ibm.icu_3.4.4.1.jar
* concurrent-10.3.jar
* iri.jar
* jena.jar
* jjtraveler-0.6.jar
* logback.jar
* org-apache-commons-logging.jar
* owlapi-api.jar
* pellet-jena.jar
* pellet-query.jar
* pellet.jar
* relaxngDatatype-2.2.jar
* shared-objects-1.4.9-p1.jar
* slf4j.jar
* xerces-2.7.1.jar
* xsdlib-1.5.jar

**Similarities** can be defined according to the ontology _similarity_ (examples in the file similarity.owl).

A **full similarity** (or **similarity-on-undividual**) can be defined as follows:

    <owl:NamedIndividual rdf:about="http://www.semanticweb.org/flavio/2016/4/similarity#un1">
        <rdf:type rdf:resource="http://www.flaviopileggi.net/Ontology/similarity#uncertainElement"/>
        <similarTo rdf:resource="http://www.semanticweb.org/flavio/2016/4/UC1#Flavio"/>
    </owl:NamedIndividual> 

That OWL statement defines un1 as an uncertainty (uncertainElement) similar to Flavio (in the context of the ontology UC1).

**Partial similarity** (or **similarity-on-property**) can be defined as in the example below:

    <owl:NamedIndividual rdf:about="http://www.semanticweb.org/flavio/2016/4/similarity#un1">
        <rdf:type rdf:resource="http://www.flaviopileggi.net/Ontology/similarity#uncertainElement"/>
        <similarTo rdf:resource="http://www.semanticweb.org/flavio/2016/4/UC1#Flavio"/>
        <onProperty rdf:datatype="http://www.w3.org/2001/XMLSchema#string">http://www.semanticweb.org/flavio/2016/4/UC1#detectedAt</onProperty>
        <onProperty rdf:datatype="http://www.w3.org/2001/XMLSchema#string">http://www.semanticweb.org/flavio/2016/4/UC1#lives</onProperty>
    </owl:NamedIndividual>

The individual un1 is stated similar to Flavio only on the properties detectedAt and lives.

Finally, **similarity-on-value** can be defined as follows:

    <owl:NamedIndividual rdf:about="http://www.semanticweb.org/flavio/2016/4/similarity#un5">
        <rdf:type rdf:resource="http://www.flaviopileggi.net/Ontology/similarity#uncertainElement"/>
        <similarTo rdf:resource="http://www.semanticweb.org/flavio/2016/4/UC1#Mary"/>
        <onValue rdf:datatype="http://www.w3.org/2001/XMLSchema#string">http://www.w3.org/1999/02/22-rdf-syntax-ns#type,http://www.semanticweb.org/flavio/2016/4/UC1#Surfer</onValue>
    </owl:NamedIndividual>
    
The individual un5 is declared similar to Mary but also of type surfer.

The class example.java includes a simple code to reason on the similarities (defined in similarity.owl) in the context of the example ontology in the file UC1.owl.

Current version:
* **SimilarityReasonerV1.4**: Minor changes. Improvements to usability. 

Previous versions:
* **SimilarityReasonerV1.3**: it supports the full environment including _similarity-on-individual_, _similarity-on-property_ and _similarity-on-value_. 
* **SimilarityReasonerV1.2**: it supports both _similarity-on-individual_ and _similarity-on-property_.
* **SimilarityReasonerV1.1**: it extends the V1.0 by including the _Similarity Factor_ as a quantification of the impact of uncertainty on the ecosystem.
* **SimilarityReasonerV1.0**: this version only supports _similarity-on-individual_.

Run the example below (example.java) to quickly get started:

    	
	// The CONTEXT ontology + The SIMILARITY ontology
	private String	ontology	= "file:///Users/spileggi/Desktop/UC1.owl";
	private String	similarity	= "file:///Users/spileggi/Desktop/similarity.owl";
	
	private String similarityPrefix = "http://www.flaviopileggi.net/Ontology/similarity#";
		
	//query	
	String queryString =        
	    "select ?surfer "+
	    "where { "+
	    "?surfer a <http://www.semanticweb.org/flavio/2016/4/UC1#Surfer>  "+
	    "} \n ";
				
	public void run() {

		SFactorRS rs = new SFactorRS();
		SFactorRS rs1 = new SFactorRS();
		        
		//The class similarityOnIndividual considers ONLY full similarities (similarity-on-individual)
		rs = new similarityOnIndividual(similarityPrefix).QueryOnSimilarity(ontology, similarity, queryString);
	        rs.printDeterministic(queryString);
	        rs.printUncertain(queryString);
			    
		/*The class similarityOnProperty processes both full similarities (similarity-on-individual)
		and partial similarities (similarity-on-property)*/
			
		rs1 = new similarityOnProperty(similarityPrefix).QueryOnSimilarity(ontology, similarity, queryString);
		rs1.printDeterministic(queryString);
		rs1.printUncertain(queryString);
			    
	}
	

	


### Papers
* Salvatore F. Pileggi, **_Web of Similarity_**, Journal of Computational Science (2016). https://www.sciencedirect.com/science/article/pii/S1877750316303842


