import java.io.Serializable;

public class MorraInfo implements Serializable
{
    int p1Points, p2Points;
    String p1Plays, p2Plays,p1Guess,p2Guess;
    Boolean have2Players;

    MorraInfo()
    {
        p1Points = 0;
        p2Points = 0;
        have2Players = false;
    }

    int evalPlay(int p1Fingers, int p1Guess, int p2Fingers, int p2Guess)
    {
        if(have2Players)
        {
            int total = p1Fingers + p2Fingers;

            if (p1Guess == total)
                return 1;

            else if (p2Guess == total)
                return 2;

            else
                return 0;
        }
        else
        {
            return 3;//signifies there are not two players
        }
    }
}
