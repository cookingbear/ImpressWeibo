package org.bigbear.impressweibo.bean.android;

import org.bigbear.impressweibo.support.error.WeiboException;

import android.os.Bundle;

/**
 * User: qii
 * Date: 13-4-16
 */
public class AsyncTaskLoaderResult<E> {
    public E data;
    public WeiboException exception;
    public Bundle args;
}
