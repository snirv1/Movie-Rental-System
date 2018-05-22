package bgu.spl181.net.api.bidi;

/**
 * this class represent a basic movie object in order to keep base movie in the json file
 *
 */

import java.io.Serializable;

public class BaseMovie implements Serializable {
    protected long id;
    protected String name;


    /**
     * constructor
     * @param id  movie ID
     * @param name movie name
     */
    public BaseMovie(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
