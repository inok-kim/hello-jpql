package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            em.persist(member);

//            Member member2 = new Member();
//            member.setUsername("member2");
//            em.persist(member2);

            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
            // 값이 하나 - 결과가 정확히 하나, 결과가 없으면 NoResultException, 둘 이상 NonUniqueResultException
            Member singleResult = query1.getSingleResult();

            // 값이 여러개 - 결과가 없으면 빈 리스트 반환
            List<Member> resultList = query1.getResultList();
            for (Member member1 : resultList) {

            }

            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
            Query query3 = em.createQuery("select m.username, m.age from Member m");

            // 이름 기준 파라미터 바인딩, 보통은 메서드 체인을 이용
            TypedQuery<Member> byUsernameQuery = em.createQuery("select m from Member m where m.username =:username", Member.class);
            byUsernameQuery.setParameter("username", "member1");
            Member singleResultByUsername = byUsernameQuery.getSingleResult();
            System.out.println("singleResultByUsername = " + singleResultByUsername.getUsername());

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();;
    }
}
