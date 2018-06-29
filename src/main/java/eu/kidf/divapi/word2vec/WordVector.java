/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.kidf.divapi.word2vec;

/**
 *
 * @author gabor
 */
class WordVector<T> {
    private String word;
    private T vector;
    
    public WordVector(String w, T v) {
        word = w;
        vector = v;
    }
    
    public String getWord() {
        return word;
    }
    
    public T getVector() {
        return vector;
    }
}
