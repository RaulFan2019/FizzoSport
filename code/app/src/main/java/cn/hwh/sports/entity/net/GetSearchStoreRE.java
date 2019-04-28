package cn.hwh.sports.entity.net;

import java.util.List;

/**
 * Created by Raul.Fan on 2017/1/10.
 */

public class GetSearchStoreRE {


    /**
     * count : 1
     * stores : [{"id":3,"storeid":100003,"name":"软件组","description":"软件组","logo":"http://7xk0si.com1.z0.glb.clouddn.com/2016-10-12_57fe064e687ec.","address":""}]
     */

    public int count;
    /**
     * id : 3
     * storeid : 100003
     * name : 软件组
     * description : 软件组
     * logo : http://7xk0si.com1.z0.glb.clouddn.com/2016-10-12_57fe064e687ec.
     * address :
     */

    public List<StoresEntity> stores;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<StoresEntity> getStores() {
        return stores;
    }

    public void setStores(List<StoresEntity> stores) {
        this.stores = stores;
    }

    public static class StoresEntity {
        public int id;
        public int storeid;
        public String name;
        public String description;
        public String logo;
        public String address;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStoreid() {
            return storeid;
        }

        public void setStoreid(int storeid) {
            this.storeid = storeid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
