package javax.persistence;

/**
 * Created by cyril on 8/28/13.
 */
public @interface OneToMany {

    public Class<?> targetEntity();

}
