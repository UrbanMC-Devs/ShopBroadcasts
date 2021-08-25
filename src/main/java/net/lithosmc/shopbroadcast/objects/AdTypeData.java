package net.lithosmc.shopbroadcast.objects;

public class AdTypeData implements Cloneable {
    private String type = null;
    private String data = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean hasType() {
        return type != null;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        // Store blank data as null
        if (data != null && data.isBlank())
            data = null;

        this.data = data;
    }

    public boolean hasData() {
        return data != null;
    }

    @Override
    public AdTypeData clone() {
        var newAdData = new AdTypeData();
        newAdData.data = this.data;
        newAdData.type = this.type;
        return newAdData;
    }
}
