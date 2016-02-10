package com.gmail.dajinchu.stem.model;

import com.orm.SugarRecord;

/**
 * Created by Da-Jin on 2/5/2016.
 */
public abstract class ChildRecord extends SugarRecord{

    protected abstract ParentRecord getParent();

    @Override
    public long save() {
        long save = super.save();
        getParent().childUpdated();
        return save;
    }
}
