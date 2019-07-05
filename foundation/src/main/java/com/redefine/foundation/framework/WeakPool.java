package com.redefine.foundation.framework;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WeakPool<T> {
    private final ReferenceQueue<T> queue = new ReferenceQueue<>();
    private final List<WeakReference<T>> weakList = new ArrayList<>();

    public int size() {
        return weakList.size();
    }

    public T get(int index) {
        T obj = null;
        if (index >= 0 && index < weakList.size()) {
            if (weakList.get(index) != null) {
                obj = weakList.get(index).get();
            }
        }
        return obj;
    }

    public void add(T object) {
        if (contain(object) == -1) {
            WeakReference<T> ref = new WeakReference<>(object, queue);
            weakList.add(ref);
        }
    }

    public void remove(T object) {
        int idx = contain(object);
        if (idx != -1) {
            weakList.remove(idx);
        }
    }

    public void removeAll() {
        weakList.clear();
    }

    private int contain(T object) {
        for (int i = 0; i < weakList.size(); i++) {
            T obj = null;
            if (weakList.get(i) != null) {
                obj = weakList.get(i).get();
            }
            if (obj != null && obj == object) return i;
        }
        return -1;
    }

}
