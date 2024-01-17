package study.datajpa.repository.projection;

public interface NestedClosedProjection {

    String getUsername(); //root이기 때문에 정확하게 잘 가져옴

    TeamInfo getTeam(); //root가 아니기 때문에 LEFT OUTER JOIN 처리

    interface TeamInfo{
        String getName();
    }
}
