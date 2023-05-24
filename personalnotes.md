# Egna anteckningar, inte i rapportformat
**MainActivity**:

Klassen MainActivity är appens ingångspunkt. Den ställer in layouten, initierar RecyclerView och hämtar JSON-data från en URL med hjälp av klassen JsonTask. Den hanterar också användarinteraktion genom att sätta klicklyssnare för sorteringsknapparna. Metoden onPostExecute tar emot JSON-svaret, analyserar det till en lista av inputDecoder-objekt och fyller RecyclerView med hjälp av klassen RecyclerViewAdapter. Metoden setSortOrder uppdaterar sorteringsordningen, sparar den i delade inställningar och utlöser sortering och filtrering av listan.

**RecyclerViewAdapter**:

Klassen RecyclerViewAdapter utökar RecyclerView.Adapter och binder data till vyerna i RecyclerView. Den tar en lista av inputDecoder-objekt och ett OnClickListener-gränssnitt som argument i konstruktorn. Metoden onCreateViewHolder inflaterar layouten för varje element, och metoden onBindViewHolder binder data till vyerna. Klassen ViewHolder håller referenser till vyerna och implementerar klicklyssnaren. Den definierar också ett gränssnitt för att hantera klickhändelser för elementen.

**inputDecoder**:

Klassen inputDecoder representerar ett element i RecyclerView. Den har egenskaper som ID, namn, plats, kategori och auxdata. Konstruktorn tar emot ett JSON-objekt och extraherar värdena för att initialisera egenskaperna. Klassen tillhandahåller getter-metoder för att komma åt egenskaperna och en getDescription-metod för att få en formaterad beskrivning av elementet.

Sammanfattningsvis ställer MainActivity-klassen in appen, RecyclerViewAdapter-klassen hanterar databindning och inputDecoder-klassen representerar elementen i RecyclerView. Tillsammans ger de funktionalitet för att visa och interagera med listan av element i appen.

