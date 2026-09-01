package com.judomasters.game;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.view.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {
    public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new JudoView(this));
    }
}

class Fighter {
    String name,country,special;
    int power,speed,technique,balance,stamina,defense;
    Fighter(String n,String c,String s,int p,int sp,int t,int b,int st,int d){
        name=n;country=c;special=s;power=p;speed=sp;technique=t;balance=b;stamina=st;defense=d;
    }
}

class JudoView extends View {
    Paint p=new Paint(1); Random rng=new Random();
    Fighter[] f={
      new Fighter("Renzo","Japão","Seoi-nage",82,78,92,88,80,84),
      new Fighter("Aiko","Japão","Uchi-mata",78,90,94,91,76,86),
      new Fighter("Mateo","Espanha","O-goshi",91,70,83,79,88,78),
      new Fighter("Kenji","Japão","O-soto-gari",87,75,89,84,85,88),
      new Fighter("Lucas","Portugal","Harai-goshi",84,82,86,87,81,82),
      new Fighter("Yuki","Japão","Ko-uchi-gari",74,94,91,93,75,89),
      new Fighter("Diego","Brasil","Uchi-mata",80,88,88,85,83,80),
      new Fighter("Hana","Japão","Seoi-nage",77,92,95,90,78,91),
      new Fighter("Omar","Angola","O-goshi",90,73,80,82,92,76),
      new Fighter("Sora","Japão","O-soto-gari",86,84,90,86,84,87),
      new Fighter("Tiago","Angola","Harai-goshi",83,85,84,89,87,83),
      new Fighter("Mika","Japão","Ko-soto-gake",76,89,93,92,79,90),
      new Fighter("Riku","Japão","Uchi-mata",81,86,91,88,82,85),
      new Fighter("Bruno","Brasil","O-goshi",93,68,81,77,94,74),
      new Fighter("Emi","Japão","Seoi-nage",75,91,96,94,77,92)
    };
    int screen=0,selected=0,opp=1;
    boolean[] team=new boolean[15];
    ArrayList<Integer> enemyTeam=new ArrayList<>();
    int myWins=0,enemyWins=0,match=0;
    float meHP=100,opHP=100,meBal=100,opBal=100;
    String message="Escolhe uma ação.";

    JudoView(Context c){super(c); p.setTypeface(Typeface.DEFAULT); setFocusable(true);}

    void text(Canvas c,String s,float x,float y,float size){p.setTextSize(size);p.setColor(Color.WHITE);c.drawText(s,x,y,p);}
    void rect(Canvas c,float l,float t,float r,float b,int color){p.setColor(color);c.drawRoundRect(l,t,r,b,18,18,p);}
    void title(Canvas c,String s){text(c,"🥋 JUDO MASTERS",28,42,28);text(c,s,28,80,22);}

    protected void onDraw(Canvas c){
        c.drawColor(Color.rgb(18,18,22));
        if(screen==0) menu(c);
        else if(screen==1) individual(c);
        else if(screen==2) teamSelect(c);
        else if(screen==3) battle(c);
        else result(c);
    }

    void menu(Canvas c){
        title(c,"MENU PRINCIPAL");
        rect(c,180,130,680,205,Color.rgb(150,35,35)); text(c,"🥋 LUTA INDIVIDUAL",270,178,24);
        rect(c,180,225,680,300,Color.rgb(35,90,150)); text(c,"👥 LUTA POR EQUIPA 5 × 5",245,273,23);
        rect(c,180,320,680,395,Color.rgb(55,105,65)); text(c,"🏆 TORNEIO",325,368,24);
        rect(c,180,415,680,490,Color.rgb(105,80,30)); text(c,"🎯 TREINO",325,463,24);
        text(c,"15 judocas • judogi • combate por toque",205,540,18);
    }

    void cards(Canvas c, boolean teamMode){
        for(int i=0;i<15;i++){
            int col=i%5,row=i/5; float x=25+col*155,y=105+row*105;
            int color=teamMode && team[i]?Color.rgb(25,125,70):(i==selected?Color.rgb(175,125,25):Color.rgb(47,47,55));
            rect(c,x,y,x+140,y+88,color);
            text(c,f[i].name,x+12,y+28,19);
            text(c,f[i].country+"  "+f[i].special,x+12,y+52,12);
            text(c,"Técnica "+f[i].technique+"  DEF "+f[i].defense,x+12,y+73,11);
        }
    }

    void individual(Canvas c){
        title(c,"ESCOLHA O TEU JUDOCA");
        cards(c,false);
        rect(c,690,430,900,495,Color.rgb(150,35,35)); text(c,"LUTAR",755,475,23);
    }

    void teamSelect(Canvas c){
        title(c,"EQUIPA — ESCOLHE 5 JUDOCAS");
        cards(c,true);
        int n=0; for(boolean b:team)if(b)n++;
        text(c,"A tua equipa: "+n+"/5",700,130,21);
        rect(c,690,165,900,235,Color.rgb(55,85,145));text(c,"CPU: 5 ALEATÓRIOS",715,208,17);
        rect(c,690,430,900,495,n==5?Color.rgb(30,120,70):Color.rgb(70,70,75));
        text(c,"COMEÇAR",750,475,21);
    }

    void setupBattle(boolean isTeam){
        meHP=100;opHP=100;meBal=100;opBal=100;message="A luta começou!";
        if(isTeam){
            enemyTeam.clear();
            ArrayList<Integer> pool=new ArrayList<>();
            for(int i=0;i<15;i++)if(!team[i])pool.add(i);
            Collections.shuffle(pool,rng);
            for(int i=0;i<5;i++)enemyTeam.add(pool.get(i));
            match=0;myWins=0;enemyWins=0;
        } else {
            do{opp=rng.nextInt(15);}while(opp==selected);
        }
        screen=3;
    }

    int myF(){ if(!hasTeam()) return selected; int k=0; for(int i=0;i<15;i++)if(team[i]){if(k==match)return i;k++;}return selected; }
    int enF(){ return hasTeam()?enemyTeam.get(match):opp; }
    boolean hasTeam(){for(boolean b:team)if(b)return true;return false;}

    void battle(Canvas c){
        Fighter a=f[myF()], b=f[enF()];
        title(c,hasTeam()?"COMBATE "+(match+1)+"/5":"LUTA INDIVIDUAL");
        rect(c,35,105,435,315,Color.rgb(232,232,232)); rect(c,505,105,905,315,Color.rgb(205,205,215));
        p.setColor(Color.BLACK);c.drawCircle(235,185,45,p);c.drawCircle(705,185,45,p);
        text(c,a.name+" • "+a.country,65,260,22);text(c,b.name+" • "+b.country,535,260,22);
        text(c,"VIDA",55,345,16); text(c,"VIDA",525,345,16);
        rect(c,55,355,405,382,Color.DKGRAY);rect(c,525,355,875,382,Color.DKGRAY);
        rect(c,55,355,55+3.5f*meHP,382,Color.rgb(40,160,80));
        rect(c,525,355,525+3.5f*opHP,382,Color.rgb(175,45,45));
        text(c,"Equilíbrio "+(int)meBal,55,410,15);text(c,"Equilíbrio "+(int)opBal,525,410,15);
        text(c,message,250,440,17);
        rect(c,35,465,205,535,Color.rgb(45,95,155));text(c,"AGARRAR",77,507,18);
        rect(c,220,465,390,535,Color.rgb(155,45,45));text(c,"PROJETAR",260,507,18);
        rect(c,405,465,575,535,Color.rgb(60,110,70));text(c,"DEFENDER",443,507,18);
        rect(c,590,465,760,535,Color.rgb(145,105,25));text(c,"ESPECIAL",625,507,18);
        rect(c,775,465,905,535,Color.rgb(85,70,120));text(c,"CHÃO",815,507,18);
    }

    void act(int n){
        Fighter a=f[myF()],b=f[enF()];
        if(n==0){ opBal-=Math.max(5,a.technique/12.0); meBal-=3; message="Pegada firme! O adversário perdeu equilíbrio."; }
        if(n==1){
            double chance=(a.technique+a.balance+(100-opBal))/3.0;
            if(rng.nextInt(100)<chance){opHP-=Math.max(18,a.power/3.0);opBal-=25;message="PROJEÇÃO! "+a.special+" — Waza-ari!";}
            else {meBal-=12;message="A projeção falhou. O adversário defendeu.";}
        }
        if(n==2){meBal=Math.min(100,meBal+18);message="Defesa preparada."; }
        if(n==3){
            double chance=(a.technique+a.speed)/2.0;
            if(rng.nextInt(100)<chance){opHP-=30;opBal-=30;message="ESPECIAL! "+a.special+" — grande queda!";}
            else message="O adversário escapou do especial.";
        }
        if(n==4){opHP-=12;opBal-=20;message="Combate no chão! Imobilização iniciada."; }
        // CPU responde
        if(opHP>0){
            int ai=rng.nextInt(4);
            if(ai==0)meBal-=5;
            else if(ai==1)meHP-=Math.max(4,b.power/15.0);
            else if(ai==2)meHP-=7;
            meBal=Math.max(0,meBal);meHP=Math.max(0,meHP);
        }
        opHP=Math.max(0,opHP);opBal=Math.max(0,opBal);
        if(opHP<=0 || meHP<=0){
            boolean win=opHP<=0 && meHP>0;
            if(hasTeam()){
                if(win)myWins++; else enemyWins++;
                match++;
                if(match>=5 || myWins>=3 || enemyWins>=3){screen=4;}
                else {meHP=100;opHP=100;meBal=100;opBal=100;message="Próximo combate!";}
            } else screen=4;
        }
        invalidate();
    }

    void result(Canvas c){
        title(c,hasTeam()?"RESULTADO DA EQUIPA":"RESULTADO");
        boolean win=hasTeam()?myWins>enemyWins:opHP<=0;
        text(c,win?"🏆 VITÓRIA!":"DERROTA",310,210,40);
        if(hasTeam()) text(c,"Placar final: "+myWins+" — "+enemyWins,330,270,25);
        rect(c,300,360,600,435,Color.rgb(55,90,150));text(c,"MENU PRINCIPAL",355,407,21);
    }

    public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()!=MotionEvent.ACTION_UP)return true;
        float x=e.getX(),y=e.getY();
        if(screen==0){
            if(y>120&&y<215){teamMode(false);}
            else if(y>215&&y<310){teamMode(true);}
        } else if(screen==1){
            for(int i=0;i<15;i++){int col=i%5,row=i/5;float xx=25+col*155,yy=105+row*105;
                if(x>xx&&x<xx+140&&y>yy&&y<yy+88)selected=i;}
            if(x>690&&y>420)setupBattle(false);
        } else if(screen==2){
            for(int i=0;i<15;i++){int col=i%5,row=i/5;float xx=25+col*155,yy=105+row*105;
                if(x>xx&&x<xx+140&&y>yy&&y<yy+88){
                    int n=0;for(boolean z:team)if(z)n++;
                    if(team[i]||n<5)team[i]=!team[i];
                }}
            int n=0;for(boolean z:team)if(z)n++;
            if(n==5&&x>680&&y>420)setupBattle(true);
        } else if(screen==3){
            if(y>455&&y<550){
                if(x<210)act(0);else if(x<400)act(1);else if(x<585)act(2);else if(x<770)act(3);else act(4);
            }
        } else if(screen==4){
            if(x>280&&x<620&&y>340){screen=0;}
        }
        invalidate();return true;
    }
    void teamMode(boolean b){screen=b?2:1;}
}
