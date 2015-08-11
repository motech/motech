Module to load seed data into CouchDB via Ektorp.


# Introduction #

Module motech-seed in motech-delivery project (https://github.com/motech/motech-delivery) provides the seed loading mechanism.

# Features #

  * Annotation Driven.
  * Seeds can be loaded on a priority basis.

# Example #
Regimen need preloaded Drugs:

@Component<br>
public class DrugSeed {<br>
<br>
<blockquote>@Autowired<br>
private AllDrugs allDrugs;</blockquote>

<blockquote>@Seed(priority = 1)<br>
public void load() throws InterruptedException {<br>
<blockquote>Drug drug1 = new Drug("D1");<br>
Drug drug2 = new Drug("D2");<br>
allDrugs.add(drug1);<br>
allDrugs.add(drug2);<br>
</blockquote>}<br>
}<br>
<br>
@Component<br>
public class RegimenSeed {<br>
@Autowired<br>
private AllRegimens allRegimens;</blockquote>

<blockquote>@Autowired<br>
private AllDrugs allDrugs;</blockquote>

<blockquote>@Seed(priority = 0)<br>
public void load() throws InterruptedException {<br>
<blockquote>Regimen regimen = new Regimen("regimen", allDrugs.getAll());<br>
allRegimens.add(regimen);<br>
</blockquote>}<br>
}</blockquote>

#3<br>
Change in platform client (tama, telco...) pom.xml:<br>
<blockquote>

<target>

<br>
<br>
<property name="compile_classpath" refid="maven.compile.classpath"/><br>
<br>
<br>
<br>
<java classname="org.motechproject.deliverytools.seed.SetupSeedData"><br>
<br>
<br>
<br>
<classpath><br>
<br>
<br>
<br>
<pathelement path="${compile_classpath}"/><br>
<br>
<br>
<br>
</classpath><br>
<br>
<br>
<br>
</java><br>
<br>
<br>
<br>
</target><br>
<br>
