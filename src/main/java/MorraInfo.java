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
        p1Plays = "";
        p2Plays = "";
        p1Guess = "";
        p2Guess = "";

        have2Players = false;
    }

    int evalPlay(int p1Fingers, int p1Guess, int p2Fingers, int p2Guess)
    {
        if(have2Players)
        {
            int total = p1Fingers + p2Fingers;

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
        //this else statement signifies that there aren't two players connected
        else
        {
            return 3;//signifies there are not two players
        }
    }
}