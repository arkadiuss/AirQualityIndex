package cache;

public class CacheService implements ICacheService {

    private String storageName;

    public CacheService(String storageName){
        this.storageName = storageName;
    }


}
