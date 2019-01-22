# AssimilationMC
## A big project.

###Biography
This was a project which I started late 2016 after wanting to pursue the UHC (Ultra HardCore) server type for Minecraft, as it was very popular at that time. However since it took me so long for this project to be released, it didn't really have the same effect. I am happy with the most of the outcome as I felt it helped improve my programming skills and opened my experience to new things.

####v1
The first version of AssimilationMC (If you look into "###ARCHIVE###/#OLD Assimilation" on the root directory of this repository you will see another directory name "###ARCHIEVE###" you will find it in there) was made for a BungeeCord (software which allowed for multiple servers to connect to each other) network and had basic features made at a, in hindsight, bad way. The first version utilized seperate plugins with a core, and the plugins would hook into the core during runtime as seperate entities, but could not run without the core. AssiBPermissions was the first permissions plugin (a security implementation allowing different useres to have different permissions to different things based on their group) I had ever made, whilst it was sophisticated and specialized for AssimilationMC I was quite happy with it. This was the first idea of when there was going to be a seperate Kit PVP thing, mainly added because I was getting bored of doing the main thing.

####v2
During this time, I had still not learnt and implemented Gradle (something which I have come quite fond of). The only reason this is something is because I decided that running a network with my limited income was not sustainable so I was exploring the ways of running everything *on a single server*. You can see this is a lot more developed than v1.0 and if you look at "assidatabase" you can see I was beginning to dable in Redis to try and create an asynchronous system to handle Redis using Lettuce. This went on for longer until it came to a halt when I started a job as a Front-End developer with Pokefind, a large Minecraft network based on Pokemon, in August 2017 until Novemeber 2017 - I didn't hang around for long. However during this time I learnt so much and my style improved massively, I left because of my intention to study more on exams however I felt very incompetent compared to other people in the group. Shortly after resigning and resuming AssimilationMC, I ditched v2 and began on v3, what would become the final and what I am proud with.

####v3
To make it clear, I have never liked to make games - in Minecraft or whatever - I have no intention to design front-end to games in the future. I prefer what some would consider the 'boring' and 'geeky' stuff - backend infastructure and utility. Anyway, this is the final major version of the project which incorperated MySQL, Redis and Go (language). By far the most advanced thing I had delved into. There would be 1 main plugin and there would be seperate modules which would hook into the plugin. For the most part, everything was in AssiCore, and PVPLobbyAdaptor and UHCLobbyAdaptor were optinal adaptors which didn't necessarily fit into the core. From working at Pokefind, this project had a massive influence from the coding style that was being used there. Furthermore, I finally adopted Gradle as a dependency management after deciding I didn't like Maven - despite it being needed more. However I learnt the basics of Maven as well.
I wanted to things that I could implement such as a netty packet system however I ended up with just sticking with the Redis PubSub system as it was more convient, the idea of a netty system was inspired by prompting of one of the Pokefind developers which I became friends with. This project also has helped me develop my Regex skills with making the chat filter etc. This took most of my summer break of 2018. It was eventually released late September and to say it frankly, the release was uneventful and there were massive bugs which I couldn't have tested for with the amount of accounts despensible. Despite this, the bugs were fixed however nothing is the same as the 'official release'. Additionally, we needed an Anticheat and after purchasing it (and implementing it into the core), it appeared great in the testing however it started to misbehave and we had to supress it majorly - only the beginning of the anticheat problems.

Whilst there is still so much for me to learn, I am enthusastic for my next stage of programming wherever that will take me. Currently I have been made to self-learn C# to accomidate for making a game in Unity for my college course, which has come out fun but using the free 2D graphics is just very sad. As hinted before, I would rather just stay doing the programming despite my appeal for beutiful landscapes in games and art in general - I'm just not the person for it.

###Credits
I would like to thank 3 people in particular for joining me on the journey to make this public failure of a Minecraft network.
* Bob - We first encounted each other in late elementary school through my best friend, we didn't talk however we worked together during my first network (Antz Nest), and the second (Mango Realms) and finally the last (AssimilationMC). He has always been there to help test and develop, and even followed me into my ignorant path of giving out loans to desperate minecraft people - a bad decision.
* Dan - We first met in Antz Nest and since then we have become closer friends, I helped him in his early days of programming and during Mango Realms he did the economy system (admittily not great :P but he has improved so much since then). In AssimilationMC he has contributed to the extensive testing and some cosmetics. 
* Fish - We first met when I was doing some freelancing on a project called ThanatosPrison which was suprising realtively successful and he was a web developer. He was usually there to help test and created our website portals, CSS and themes as well as setup and maintain our forums (the code to that didn't make it here), as well as the BuyCraft theme (online shop for buying virtual items).

###Minecraft
I haven't played Minecraft since September 2018, which is what I see as an achievement. I think it is developing well as a game but for those complaining about the new things need to move on as the game is clearly not for them anymore, its good to know people grow up. Some things people don't generally like to change. In the recent past, I have started on learning projects such as Smark which was coming along well before I paused it around October. I even got some friends to help localize it for me, cool project and might return to it some time. I still work every now and then for a network called McOrigins affilated with  relatively famous YouTuber ExplodingTnT and managed by GamePlayerHD, someone who I have become what I would consider good friends with. I do projects when asked for however I keep my distance from the network itself. Its very unoften I am actually asked to do a task so that isn't a problem for me.

This turned out to be longer than I had expected. It wasn't greatly planned but this was designed to show my progress to any passerbys about the development of previous major projects. Have a good day.

Ellie






