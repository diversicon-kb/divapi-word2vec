package eu.kidf.divapi.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import eu.kidf.divapi.Concept;
import eu.kidf.divapi.Domain;
import eu.kidf.divapi.IDivAPI;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Adaptor implementing the DivAPI interface for accessing
 * word2vec-based word embeddings. Uses Deeplearning4J libraries,
 * https://deeplearning4j.org
 * 
 * @author Gábor BELLA
 */
public class Word2VecAdaptor implements IDivAPI {
    
    // the similarity theshold was set empirically
    // based on an ontology matching experiment
    public static final double SIMILARITY_THRESHOLD = 0.66;
    
    private final Word2Vec dic;
    private Double threshold;

    public Word2VecAdaptor(String pathToResource, Double threshold) throws IOException {
        this(pathToResource);
        this.threshold = threshold; 
    }

    public Word2VecAdaptor(String gloveBinaryPath) throws IOException {
        try {
            File gModel = new File(gloveBinaryPath);
            dic = WordVectorSerializer.readWord2VecModel(gModel);
            threshold = SIMILARITY_THRESHOLD;
        } catch(Exception e) {
            throw new IOException("Could not load word2vec binary vector file " + gloveBinaryPath + ". Reason: " + e.getMessage(), e);
        }
    }

    @Override
    public Set<String> getRelatedWords(String language, Domain domain, String word, WordRelation rel) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<String, Double> getRelatedWordsWeighted(String language, Domain domain, String word, WordRelation rel) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<WordRelation> getRelations(String language, Domain domain, String word1, String word2) {
        Set<WordRelation> relSet = new HashSet<>();
        Map<WordRelation, Double> relMap = getRelationsWeighted(language, domain, word1, word2);
        for(WordRelation rel : relMap.keySet()) {
            if (relMap.get(rel) < threshold) {
                relSet.add(rel);
            }
        }
        return relSet;
    }

    @Override
    public Map<WordRelation, Double> getRelationsWeighted(String language, Domain domain, String word1, String word2) {
        Map<WordRelation, Double> relMap = new HashMap<>();
        Double sim = normalizeSimilarity(dic.similarity(word1, word2));
        relMap.put(IDivAPI.WORD_RELATEDNESS, sim);
        relMap.put(IDivAPI.WORD_SIMILARITY, sim);
        return relMap;
    }

    @Override
    public Set<String> getLanguages(Domain domain, String word) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Domain> getDomains(String language, String word) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Domain, Double> getDomainsWeighted(String language, String word, Set<Domain> domains) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Concept> getConcepts(String language, Domain domain, String word) {
        Set<Concept> result = new HashSet<>();
        WordVector vector = getWordVector(word);
        if (vector != null) {
            result.add(new Concept(vector, word));
        }
        return result;
    }

    @Override
    public Map<Concept, Double> getConceptsWeighted(String language, Domain domain, String word) {
        Map<Concept, Double> conceptMap = new HashMap<>();
        Set<Concept> conceptSet = getConcepts(language, domain, word);
        if (conceptSet.isEmpty()) {
            return conceptMap;
        }
        Concept concept = conceptSet.iterator().next();
        conceptMap.put(concept, 1.0);
        return conceptMap;
    }

    @Override
    public Set<Concept> getConstrainedConcepts(String language, Domain domain, String word, Concept hypernymConcept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Concept, Double> getConstrainedConceptsWeighted(String language, Domain domain, String word, Concept hypernymConcept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<String> getWords(String language, Concept concept) {
        Set<String> words = new HashSet<>();
        if (concept == null) {
            return null;
        }
        words.add(concept.getID());
        return words;
    }

    @Override
    public Map<String, Double> getWordsWeighted(String language, Concept concept) {
        Map<String, Double> words = new HashMap<>();
        if (concept == null) {
            return null;
        }
        words.put(concept.getID(), 1.0);
        return words;
    }

    @Override
    public String getGloss(String language, Concept concept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Concept> getRelatedConcepts(Concept concept, Set<ConceptRelation> relations) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Concept, Double> getRelatedConceptsWeighted(Concept concept, Set<ConceptRelation> relations) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<ConceptRelation> getRelations(Concept c1, Concept c2) {
        Map<ConceptRelation, Double> relMap = getRelationsWeighted(c1, c2);
        Set<ConceptRelation> relSet = new HashSet<>();
        for(ConceptRelation rel : relMap.keySet()) {
            if (relMap.get(rel) < threshold) {
                relSet.add(rel);
            }
        }
        return relSet;
    }

    @Override
    public Map<ConceptRelation, Double> getRelationsWeighted(Concept c1, Concept c2) {
        Map<ConceptRelation, Double> relMap = new HashMap<>();
        if (c1 == null || c2 == null) {
            return relMap;
        }
        Map<WordRelation, Double> rels = getRelationsWeighted(null, null, c1.getID(), c2.getID());
        Double sim = rels.get(IDivAPI.WORD_SIMILARITY);
        relMap.put(IDivAPI.CONCEPT_RELATEDNESS, sim);
        relMap.put(IDivAPI.CONCEPT_SIMILARITY, sim);
        return relMap;
    }

    @Override
    public Set<String> getLanguages(Concept concept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Domain> getDomains(Concept concept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Domain, Double> getDomainsWeighted(Concept concept) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getLanguages() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Domain> getDomains() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private WordVector getWordVector(String word) {
        WordVector wordVector = new WordVector(word, word); 
        return wordVector;
    }
    
    private Double normalizeSimilarity(Double similarity) {
        if (similarity < 0.0) {
            similarity = 0.0;
        }
        if (similarity > 1.0) {
            similarity = 1.0;
        }
        return similarity;
    }

}
