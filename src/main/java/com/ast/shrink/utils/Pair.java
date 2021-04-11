package com.ast.shrink.utils;

public class Pair<K, V>{
    public final K first;
    public final V second;
    public Pair(K first, V second){
        this.first = first;
        this.second = second;
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        Pair<K, V> other = (Pair<K, V>) o;
        return this.first.equals(other.first) && this.second.equals(other.second);
    }

    public int hashCode() {
        return this.first.hashCode() * 31 + this.second.hashCode();
    }

}
