package CrossCuttingPackage;

public class Report {

    private String title;
    private StringBuilder Body;

    public Report(String title)
    {
        this.title = title;
        Reset();
    }

    public void AddLine(String l) {
        if(l == null)
            throw new IllegalArgumentException("NULL line was sent to add to the report:" + title);
        this.Body.append(l);
        this.Body.append("\n");
    }

    public void Reset()
    {
        Body = new StringBuilder();
        AddLine(title);
    }

    public String GetReport() { return Body.toString();}

    public String getTitle() {
        return title;
    }
}
