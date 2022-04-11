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
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member memberTeamA = new Member();
//            memberTeamA.setUsername("teamA");
            memberTeamA.setAge(60);
            memberTeamA.setTeam(team);
            memberTeamA.setType(MemberType.USER);
            em.persist(memberTeamA);

//            String query = "select 'a' || 'b' from Member m";
            String query = "select concat('a','b') from Member m";
            List<String> result = em.createQuery(query, String.class).getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
            }

            String locateQuery = "select locate('1', m.username) from Member m";
            List<Integer> locateResult = em.createQuery(locateQuery, Integer.class).getResultList();
            for (Integer integer : locateResult) {
                System.out.println("integer = " + integer);
            }

            String myFunctionQuery = "select function('group_concat', m.age) from Member m ";
            List<String> myFunctionResult = em.createQuery(myFunctionQuery, String.class).getResultList();
            for (String s : myFunctionResult) {
                System.out.println("s = " + s);
            }

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void caseAndCoalesceAndNullIf() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member memberTeamA = new Member();
//            memberTeamA.setUsername("teamA");
            memberTeamA.setAge(60);
            memberTeamA.setTeam(team);
            memberTeamA.setType(MemberType.USER);
            em.persist(memberTeamA);

            // 기본 CASE 식
            String query =
                    "select " +
                            "case when m.age <= 10 then '학생요금' " +
                            "     when m.age >= 60 then '경로요금' " +
                            "     else '일반요금' " +
                            "end "+
                            "from Member m";

            List<String> result = em.createQuery(query, String.class).getResultList();
            for (String fee : result) {
                System.out.println("요금 = " + fee);
            }

            // coalesce - null 이면 두 번째 파라미터 반환, null 이 아니면 username 반환
            String coalesceQuery = "select coalesce(m.username, '이름 없는 회원') as username " +
                    "from Member m";

            List<String> coalesceResult = em.createQuery(coalesceQuery, String.class).getResultList();
            for (String username : coalesceResult) {
                System.out.println("username = " + username);
            }

            // nullif - 두 번째 파라미터와 값이 같으면 null 반환
            String nullifQuery = "select nullif(m.username, 'member1') as username " +
                    "from Member m";
            List<String> nullifResult = em.createQuery(nullifQuery, String.class).getResultList();
            for (String username : nullifResult) {
                System.out.println("username = " + username);
            }

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void jpqlType() {
//        String query = "select m.username, 'HELLO', TRUE from Member m" +
//                " where m.type = :memberType";
////                            " where m.type = jpql.MemberType.ADMIN";
//        List<Object[]> resultList = em.createQuery(query)
//                .setParameter("memberType", MemberType.ADMIN)
//                .getResultList();
//        for (Object[] o : resultList) {
//            System.out.println("o[0] = " + o[0]);
//            System.out.println("o[1] = " + o[1]);
//            System.out.println("o[2] = " + o[2]);
//        }
//
//        // type으로 조회(DType)
////            em.createQuery("select i from Item i where type(i) = Book", Item.class).getResultList();

    }

    private void subquery() {

//        // JPA는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능, FROM절 서브쿼리 안 됨!
//        String selectSubquery = "select (select avg(m1.age) from Member m1) as avgAge from Member m";
//        List<Double> result = em.createQuery(selectSubquery, Double.class)
//                .getResultList();
//        for (Double age : result) {
//            System.out.println(age);
//        }
    }

    private void paging() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            for(int i=0; i<100; i++) {
                Member member = new Member();
                member.setUsername("member"+i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("result.size() = " + result.size());
            for (Member m : result) {
                System.out.println("m = " + m);
            }


            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private void projection() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

//            Member member2 = new Member();
//            member.setUsername("member2");
//            em.persist(member2);

            em.flush();
            em.clear();

            // Tuple
            List<Tuple> tupleList = em.createQuery("select m.username as username, m.age as age from Member m", Tuple.class).getResultList();
            for (Tuple tuple : tupleList) {
                String username = (String) tuple.get(0);
                int age = (int) tuple.get(1);
                String usernameByAlias = tuple.get("username", String.class);
                int ageByAlias = tuple.get("age", Integer.class);
            }

            // 스칼라 타입 프로젝션
            List nameAgeObjectList = em.createQuery("select distinct m.username, m.age from Member m").getResultList();
            for (Object o : nameAgeObjectList) {
                Object[] item = (Object[]) o;
                System.out.println(item[0]+","+item[1]);
                String username = (String) item[0];
                int age = (int) item[1];
            }

            List<Object[]> nameAgeObjectArrList = em.createQuery("select distinct m.username, m.age from Member m").getResultList();
            for (Object[] objects : nameAgeObjectArrList) {
                System.out.println(objects[0]+","+objects[1]);
                String username = (String) objects[0];
                int age = (int) objects[1];
            }

            List<MemberDTO> nameAgeDTOList = em.createQuery("select distinct new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();
            for (MemberDTO memberDTO : nameAgeDTOList) {
                String username = memberDTO.getUsername();
                int age = memberDTO.getAge();
                System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
                System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
            }

            // 임베디드 프로젝션
            em.createQuery("select o.address from Order o", Address.class).getResultList();

            // 엔티티 프로젝션
            // 명시적으로 join 을 해줘야 나중에 유지보수할때 편함
            List<Team> teamsExplicitJoin = em.createQuery("select t from Member m join m.team t", Team.class).getResultList();
            List<Team> teamsImplicitJoin = em.createQuery("select m.team from Member m", Team.class).getResultList();

            // 엔티티 프로젝션
            // entity 프로젝션으로 select 해온 엔티티들은 영속성 컨텍스트로 관리가 된다
            List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();

            Member findMember = result.get(0);
            // 업데이트 쿼리 날아감
            findMember.setAge(20);

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
