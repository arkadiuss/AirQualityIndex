package network.model.Airly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.IMappable;
import model.QualityIndex;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexAirly implements IMappable<QualityIndex> {
    private String name;
    private Double value;
    private String level;

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public String getLevel() {
        return level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    @Override
    public QualityIndex map() {
        return new QualityIndex(name, LocalDateTime.now(), level);
    }
}
