package core.util;

public class PATTERN {

    public String value = null;

    public PATTERN(String ... args)
    {
        this.value = this.join(args);
    }

    private String join(String ... args)
    {
        if (args.length > 1)
        {
            StringBuilder sb = new StringBuilder(2*args.length - 1);

            for (int idx=0; idx < args.length - 1; idx++)
            {
                sb.append(args[idx]);
                sb.append(",");
            }

            return sb.append(args[args.length - 1]).toString();
        }
        else if (args.length == 1)
        {
            return args[0];
        }
        else
        {
            return "";
        }
    }    
}
