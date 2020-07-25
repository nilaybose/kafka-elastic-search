package nb.edu.kafkaes.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRecord {
    private String id;
    private String address;
    private String region;
    private String name;

    public CustomerRecord() {
    }

    public CustomerRecord(String id, String address, String region, String name){
        this.id = id;
        this.address = address;
        this.name = name;
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public String getRegion() {
        return region;
    }

    public String getName() {
        return name;
    }
}
