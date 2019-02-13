package app.honorrollrss.repository;

import app.honorrollrss.model.Sysconfig;
import org.springframework.data.repository.CrudRepository;

public interface SysconfigRepository extends CrudRepository<Sysconfig, Integer> {
    Sysconfig findBySn(Integer sn);
}
