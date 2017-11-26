package ohtu.kauppa;

import ohtu.verkkokauppa.Kauppa;
import ohtu.verkkokauppa.Pankki;
import ohtu.verkkokauppa.Tuote;
import ohtu.verkkokauppa.Varasto;
import ohtu.verkkokauppa.Viitegeneraattori;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class VerkkokauppaTest {

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeallaAsiakkaallaJaTilinumerollaJaSummalla() {
        // luodaan ensin mock-oliot
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);
        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(matches("pekka"), anyInt(), matches("12345"), anyString(), eq(5));
        // toistaiseksi ei välitetty kutsussa käytetyistä parametreista
    }
    
    @Test
    public void kaksiTuotettaOstettuNiinTilisiirtoaKutsutaanOikein() {
        Pankki pankki = mock(Pankki.class);
        
        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "mehu", 3));
        
        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("simo", "123456");
        
        verify(pankki).tilisiirto(matches("simo"), anyInt(), matches("123456"), anyString(), eq(8));
    }
    
    @Test
    public void kaksiSamaaTuotettaOstettuNiinTilisiirtoaKutsutaanOikein() {
        Pankki pankki = mock(Pankki.class);
        
        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "mehu", 3));
        
        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.lisaaKoriin(2);
        k.tilimaksu("simo", "123456");
        
        verify(pankki).tilisiirto(matches("simo"), anyInt(), matches("123456"), anyString(), eq(6));
    }
    
    @Test
    public void kaksiTuotettaOstettuJoistaToinenPuuttuuNiinTilisiirtoaKutsutaanOikein() {
        Pankki pankki = mock(Pankki.class);
        
        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "mehu", 3));
        
        when(varasto.saldo(3)).thenReturn(0);
        when(varasto.haeTuote(3)).thenReturn(new Tuote(1, "sima", 4));
        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.lisaaKoriin(3);
        k.tilimaksu("simo", "123456");
        
        verify(pankki).tilisiirto(matches("simo"), anyInt(), matches("123456"), anyString(), eq(3));
    }
    
    @Test
    public void metodiAloitaAsiointiNollaaEdellisenOstoksenTiedot() {
        Pankki pankki = mock(Pankki.class);
        
        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "mehu", 3));
        
        when(varasto.saldo(3)).thenReturn(10);
        when(varasto.haeTuote(3)).thenReturn(new Tuote(1, "sima", 4));
        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.lisaaKoriin(3);
        k.tilimaksu("simo", "123456");
        
        verify(pankki).tilisiirto(matches("simo"), anyInt(), matches("123456"), anyString(), eq(7));
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.tilimaksu("tommi", "2345");
        
        verify(pankki).tilisiirto(eq("tommi"), anyInt(), eq("2345"), anyString(), eq(3));
    }
    
    @Test
    public void uusiViitenumeroJokaiselleMaksutapahtumalle() {
        Pankki pankki = mock(Pankki.class);
        
        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).
                thenReturn(1).
                thenReturn(2).
                thenReturn(3);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "mehu", 3));
        
        when(varasto.saldo(3)).thenReturn(10);
        when(varasto.haeTuote(3)).thenReturn(new Tuote(1, "sima", 4));
        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.lisaaKoriin(3);
        k.tilimaksu("simo", "123456");
        
        verify(pankki).tilisiirto(anyString(), eq(1), anyString(), anyString(), anyInt());
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.tilimaksu("tommi", "2345");
        
        verify(pankki).tilisiirto(anyString(), eq(2), anyString(), anyString(), anyInt());
        
        k.aloitaAsiointi();
        k.lisaaKoriin(3);
        k.tilimaksu("rami", "6789");
        
        verify(pankki).tilisiirto(anyString(), eq(3), anyString(), anyString(), anyInt());
    }

}
