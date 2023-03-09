/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class AudioDevInfoVector2 extends java.util.AbstractList<AudioDevInfo> implements java.util.RandomAccess {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected AudioDevInfoVector2(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(AudioDevInfoVector2 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_AudioDevInfoVector2(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public AudioDevInfoVector2(AudioDevInfo[] initialElements) {
    this();
    reserve(initialElements.length);

    for (AudioDevInfo element : initialElements) {
      add(element);
    }
  }

  public AudioDevInfoVector2(Iterable<AudioDevInfo> initialElements) {
    this();
    for (AudioDevInfo element : initialElements) {
      add(element);
    }
  }

  public AudioDevInfo get(int index) {
    return doGet(index);
  }

  public AudioDevInfo set(int index, AudioDevInfo e) {
    return doSet(index, e);
  }

  public boolean add(AudioDevInfo e) {
    modCount++;
    doAdd(e);
    return true;
  }

  public void add(int index, AudioDevInfo e) {
    modCount++;
    doAdd(index, e);
  }

  public AudioDevInfo remove(int index) {
    modCount++;
    return doRemove(index);
  }

  protected void removeRange(int fromIndex, int toIndex) {
    modCount++;
    doRemoveRange(fromIndex, toIndex);
  }

  public int size() {
    return doSize();
  }

  public AudioDevInfoVector2() {
    this(pjsua2JNI.new_AudioDevInfoVector2__SWIG_0(), true);
  }

  public AudioDevInfoVector2(AudioDevInfoVector2 other) {
    this(pjsua2JNI.new_AudioDevInfoVector2__SWIG_1(AudioDevInfoVector2.getCPtr(other), other), true);
  }

  public long capacity() {
    return pjsua2JNI.AudioDevInfoVector2_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    pjsua2JNI.AudioDevInfoVector2_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return pjsua2JNI.AudioDevInfoVector2_isEmpty(swigCPtr, this);
  }

  public void clear() {
    pjsua2JNI.AudioDevInfoVector2_clear(swigCPtr, this);
  }

  public AudioDevInfoVector2(int count, AudioDevInfo value) {
    this(pjsua2JNI.new_AudioDevInfoVector2__SWIG_2(count, AudioDevInfo.getCPtr(value), value), true);
  }

  private int doSize() {
    return pjsua2JNI.AudioDevInfoVector2_doSize(swigCPtr, this);
  }

  private void doAdd(AudioDevInfo x) {
    pjsua2JNI.AudioDevInfoVector2_doAdd__SWIG_0(swigCPtr, this, AudioDevInfo.getCPtr(x), x);
  }

  private void doAdd(int index, AudioDevInfo x) {
    pjsua2JNI.AudioDevInfoVector2_doAdd__SWIG_1(swigCPtr, this, index, AudioDevInfo.getCPtr(x), x);
  }

  private AudioDevInfo doRemove(int index) {
    return new AudioDevInfo(pjsua2JNI.AudioDevInfoVector2_doRemove(swigCPtr, this, index), true);
  }

  private AudioDevInfo doGet(int index) {
    return new AudioDevInfo(pjsua2JNI.AudioDevInfoVector2_doGet(swigCPtr, this, index), false);
  }

  private AudioDevInfo doSet(int index, AudioDevInfo val) {
    return new AudioDevInfo(pjsua2JNI.AudioDevInfoVector2_doSet(swigCPtr, this, index, AudioDevInfo.getCPtr(val), val), true);
  }

  private void doRemoveRange(int fromIndex, int toIndex) {
    pjsua2JNI.AudioDevInfoVector2_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
  }

}
