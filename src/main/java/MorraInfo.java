import java.io.Serializable;

public class MorraInfo implements Serializable
{
    int p1Points, p2Points,p1Plays, p2Plays,p1Guess,p2Guess;;
    int playerNumber;
    Boolean have2Players;

    MorraInfo()
    {
        p1Points = 0;
        p2Points = 0;

        have2Players = false;
    }

    int evalPlay()
    {
            int total = p1Plays + p2Plays;

            //check to see if both guessed right, award no points
            if(p1Guess == total && p2Guess == total)
                return 0;
            //check if p1 guessed right, award 1 point
            if (p1Guess == total)
                return 1;
                //check if p2 guessed right, award 1 point
            else if (p2Guess == total)
                return 2;
                //if we get here, neither guessed right, award no points
            else
                return 0;
    }
}