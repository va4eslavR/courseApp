package com.courseApp.models.repositories;

import com.courseApp.models.Rate;
import com.courseApp.models.RateAssociationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RateRepo extends JpaRepository<Rate, RateAssociationId> {

    @Modifying
    @Query(value = "INSERT INTO public.rates(postidentifier, score, readeridentifier)\n" +
                    "\tVALUES (?1, ?2, ?3);",
            nativeQuery = true)
    void insert(Long infoPostId, Integer score,String userId);
    @Modifying
    @Query(value =" UPDATE public.rates \n"+
           "\t SET  score=?1\n"+
           "\t WHERE postidentifier=?2,readeridentifier=?3;",
            nativeQuery = true)
    void updateScore(Integer score,Long infoPostId ,String userId);

    List<Rate> findByIdReaderId(String readerId);
    List<Rate> findByIdPostId(Long postId);

}
