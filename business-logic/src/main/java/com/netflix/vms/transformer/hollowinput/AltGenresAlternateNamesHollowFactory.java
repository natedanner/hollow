package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

@SuppressWarnings("all")
public class AltGenresAlternateNamesHollowFactory<T extends AltGenresAlternateNamesHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AltGenresAlternateNamesHollow(((AltGenresAlternateNamesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AltGenresAlternateNamesHollow(new AltGenresAlternateNamesDelegateCachedImpl((AltGenresAlternateNamesTypeAPI)typeAPI, ordinal), ordinal);
    }

}