package com.nemo9955.garden_revolution.game.world;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.Waves;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.entitati.Shot;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.utility.SearchAlg;
import com.nemo9955.garden_revolution.utility.SearchAlg.SANode;
import com.nemo9955.garden_revolution.utility.IndexedObject;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.stage.GameStageMaker;

public class WorldBase implements Disposable {

	public static Array<Disposable> toDispose = new Array<Disposable>(false, 1);

	private Array<ModelInstance> mediu = new Array<ModelInstance>(false, 10);
	private Array<Enemy> enemy = new Array<Enemy>(false, 10);
	private Array<Ally> ally = new Array<Ally>(false, 10);
	private Array<Shot> shot = new Array<Shot>(false, 10);
	private Array<BoundingBox> colide = new Array<BoundingBox>(false, 10);
	private Array<FightZone> fightZones = new Array<FightZone>(false, 10);
	private Array<CatmullRomSpline<Vector3>> paths;

	private Vector3 overview = new Vector3(20, 10, 10);
	private int life, money;
	private Tower[] towers;
	private boolean canWaveStart = false;
	private Waves waves;
	private String mapName;
	private Environment environment = new Environment();

	{
		// public void initEnv() {
		// lights = null;
		// lights = new Environment();
		float amb = 1f;
		float intensity = 800f;
		float height = 10f;
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, amb, amb, amb, 1f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, .1f, .5f, .9f, 1f));

		environment.add(new PointLight().set(Color.BLACK, new Vector3(0, height * 5, 0), intensity / 2f));
		environment.add(new PointLight().set(Color.BLUE, new Vector3(15.292713f, height, -29.423798f), intensity));
		environment.add(new PointLight().set(Color.RED, new Vector3(10.024132f, height, 40.54626f), intensity / 2));
		environment
				.add(new PointLight().set(Color.DARK_GRAY, new Vector3(-14.920635f, height, 0.18914032f), intensity));
		environment
				.add(new PointLight().set(Color.GREEN, new Vector3(52.817417f, height * 3, -0.45362854f), intensity));
		environment.add(new PointLight().set(Color.CYAN, new Vector3(-13.099552f, height * 2, -15.360998f), intensity));
		environment.add(new PointLight().set(Color.WHITE, new Vector3(-16.840088f, height, -17.439095f), intensity));

		// environment.set( ColorAttribute.createAmbient( 1f, 1f, 1f, 1f ) );
		// envir.add( new DirectionalLight().set( Color.WHITE, new Vector3( 1,
		// -1, 0 ) ) );
		// lights.add( new DirectionalLight().set( Color.WHITE, new Vector3(
		// 0.5f, -1, 0.4f ).nor() ) );
		// lights.add( new DirectionalLight().set( Color.WHITE, new Vector3( 0,
		// 1, 0 ) ) );
	}

	public void init(FileHandle location) {
		reset();
		mapName = location.path();
		populateWorld(location);
		readData(location);
	}

	public void init(StartingServerInfo info) {
		init(Gdx.files.internal(info.path));

		for (String str : info.turnuri) {
			String[] separ = str.split(Vars.stringSeparator);
			// System.out.println( "[C] unu din turnuri " +str );
			Tower turn = towers[Integer.parseInt(separ[0])];
			turn.upgradeTower(TowerType.valueOf(separ[1]));
			if (separ.length == 3)
				turn.changeWeapon(WeaponType.valueOf(separ[2]));
		}

		for (String str : info.players) {
			String[] separ = str.split(Vars.stringSeparator);
			towers[Integer.parseInt(separ[0])].ocupier = separ[1];
		}
	}

	public StartingServerInfo getWorldInfo(StartingServerInfo out) {
		out.path = mapName;
		int size = 0, nrTrn = 0, ply = 0, nrPl = 0;
		for (Tower trn : towers) {
			if (trn.type != TowerType.FUNDATION)
				size++;
			if (trn.ocupier != null)
				ply++;
		}

		String[] formater = new String[size];
		String[] players = new String[ply];
		for (int i = 0; i < towers.length; i++) {
			if (towers[i].type != TowerType.FUNDATION) {
				formater[nrTrn] = "" + i + Vars.stringSeparator + towers[i].type.toString();
				if (towers[i].hasWeapon())
					formater[nrTrn] += Vars.stringSeparator + towers[i].getWeapon().type.toString();
				// System.out.println( "[S] : " +formater[nrTrn] );
				nrTrn++;
			}
			if (towers[i].ocupier != null) {
				players[nrPl] = "" + i + Vars.stringSeparator + towers[i].ocupier;
				nrPl++;
			}
		}
		out.turnuri = formater;
		out.players = players;

		return out;
	}

	public void update(float delta) {

		if (canWaveStart() && (WorldWrapper.instance.isSinglelayer() || true || GR.mp.isHost()))
			if (!waves.finishedWaves())
				waves.update(delta);
			else {
				// TODO add event when game is won
			}

		for (FightZone fz : getFightZones())
			fz.update(delta);

		for (Tower trn : towers)
			trn.update(delta);

		for (Enemy fo : getEnemy())
			fo.update(delta);

		for (Ally al : getAlly())
			al.update(delta);

		for (Shot sh : getShot())
			sh.update(delta);

	}

	public void render(ModelBatch modelBatch, DecalBatch decalBatch) {
		for (ModelInstance e : mediu)
			modelBatch.render(e, environment);

		for (Enemy e : getEnemy())
			e.render(modelBatch, environment, decalBatch);

		for (Ally e : getAlly())
			e.render(modelBatch, environment, decalBatch);

		for (Shot e : getShot())
			e.render(modelBatch, environment, decalBatch);

		for (Tower tower : towers)
			tower.render(modelBatch, environment, decalBatch);

	}

	public void renderDebug(PerspectiveCamera cam, ShapeRenderer shape) {

		// shape.setColor(1, 0.5f, 0, 1);
		// for (BoundingBox box : getColide())
		// shape.box(box.min.x, box.min.y, box.max.z,
		// box.getDimensions(GR.temp4).x, box.getDimensions(GR.temp4).y,
		// box.getDimensions(GR.temp4).z);

		// shape.setColor(0.7f, 0.8f, 0.4f, 1);
		// for (Tower tower : towers)
		// for (BoundingBox box : tower.getTowerColiders())
		// shape.box(box.min.x, box.min.y, box.max.z,
		// box.getDimensions(GR.temp4).x, box.getDimensions(GR.temp4).y,
		// box.getDimensions(GR.temp4).z);

		// shape.setColor( 1, 0, 0, 1 );
		// for (Entity e : getEnemy() )
		// shape.box( e.box.min.x, e.box.min.y, e.box.max.z,
		// e.box.getDimensions().x, e.box.getDimensions().y,
		// e.box.getDimensions().z );

		// shape.setColor( 0, 0, 1, 1 );
		// for (Entity e : getAlly() )
		// shape.box( e.box.min.x, e.box.min.y, e.box.max.z,
		// e.box.getDimensions().x, e.box.getDimensions().y,
		// e.box.getDimensions().z );

		// shape.setColor( 0, 1, 1, 1 );
		// for (Entity e : getShot() )
		// shape.box( e.box.min.x, e.box.min.y, e.box.max.z,
		// e.box.getDimensions().x, e.box.getDimensions().y,
		// e.box.getDimensions().z );

		// shape.setColor(0, 0.5f, 0.5f, 1);
		// for (FightZone e : getFightZones())
		// shape.box(e.box.min.x, e.box.min.y, e.box.max.z,
		// e.box.getDimensions(GR.temp4).x,
		// e.box.getDimensions(GR.temp4).y, e.box.getDimensions(GR.temp4).z);

		shape.setColor(0f, 0f, 0f, 0.1f);
		for (SANode e : salg.getNodeField()) {
			for (SANode n : e.getNeighbors())
				shape.line(e.getPoint(), n.getPoint());
			shape.point(e.getPoint().x, e.getPoint().y, e.getPoint().z);
		}

		shape.setColor(0.0f, 0.1f, 1f, 1);
		int pts = getPaths().size;
		for (int i = 0; i < pts; i++) {
			float val = 0;
			getPaths().get(i).valueAt(GR.temp3, val);
			GR.temp3.add(i);
			while (val < 1f) {
				val += 1f / 150f;
				getPaths().get(i).valueAt(GR.temp2, val);
				GR.temp2.add(i);
				// shape.setColor(1 - i * pts / 4, 1 - i * pts / 5, 1 - i * pts
				// / 7, 1f);
				shape.setColor(col[i]);
				shape.line(GR.temp3, GR.temp2);
				GR.temp3.set(GR.temp2);
			}
		}

	}

	Color[] col = { Color.BLUE, Color.RED, Color.ORANGE, Color.TEAL, Color.OLIVE };

	private void populateWorld(FileHandle location) {
		Array<Array<IndexedObject<Vector3>>> cp = null;
		String[] sect;

		Element map = null;
		int noOfPaths = 0;
		try {
			map = new XmlReader().parse(location);
			towers = new Tower[1 + map.getInt("turnuri")];
			noOfPaths = map.getInt("drumuri");
			paths = new Array<CatmullRomSpline<Vector3>>(true, noOfPaths);
			cp = new Array<Array<IndexedObject<Vector3>>>(noOfPaths);

			for (int k = 0; k < noOfPaths; k++)
				cp.add(new Array<IndexedObject<Vector3>>(false, 1, IndexedObject.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Model scena = new G3dModelLoader(new UBJsonReader())
				.loadModel(location.parent().parent().child("maps").child(map.get("map")));

		BoundingBox pathZone = new BoundingBox();

		for (int i = 0; i < scena.nodes.size; i++) {
			String id = scena.nodes.get(i).id;
			ModelInstance instance = new ModelInstance(scena, id);
			// Node node = instance.getNode( id );
			// instance.transform.set( node.globalTransform );
			// node.translation.set( 0, 0, 0 );
			// node.scale.set( 1, 1, 1 );
			// node.rotation.idt();
			// instance.calculateTransforms();

			// System.out.println(id);

			if (id.startsWith("turn")) {
				sect = id.split("_");
				int no = Integer.parseInt(sect[1]) - 1;
				towers[no + 1] = new Tower(TowerType.FUNDATION, WorldWrapper.instance, scena.nodes.get(i).translation,
						no + 1);
			} else if (id.startsWith("path")) {
				sect = id.split("_");
				System.out.println(id + " " + scena.nodes.get(i).translation);
				int pat = Integer.parseInt(sect[1]) - 1;// TODO get rid of the
														// -1 so the paths can
														// start from 0
				int pct = Integer.parseInt(sect[2]);
				cp.get(pat).add(new IndexedObject<Vector3>(scena.nodes.get(i).translation, pct));
				pathZone.ext(scena.nodes.get(i).translation);
			} else if (id.startsWith("colider")) {
				BoundingBox box = new BoundingBox();
				instance.calculateBoundingBox(box);
				addToColide(box);
			} else if (id.endsWith("solid")) {
				BoundingBox box = new BoundingBox();
				instance.calculateBoundingBox(box);
				addToColide(box);
				mediu.add(instance);
			} else if (id.startsWith("camera")) {
				overview.set(scena.nodes.get(i).translation);
			} else if (id == "Sphere" || id == "Icosphere") {
				System.out.println("DING !!!!!!!!");
			} else {
				mediu.add(instance);
			}

		}

		salg = new SearchAlg();

		for (Array<IndexedObject<Vector3>> pat : cp)
			pat.sort();

		for (Array<IndexedObject<Vector3>> pat : cp) {
			salg.addStarting(new SANode(pat.first().object));
		}

		salg.setGoal(new SANode(cp.random().peek().object));
		System.out.println("END : " + salg.getGoalPoint());
		System.out.println("BOX : " + pathZone);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		final int pointsToGenerate = 40;
		final int minNodes = 20;
		final int minNe = 4;
		final int maxNe = 9;
		// Array<Vector3> points = new Array<>(pointsToGenerate);

		Vector3 rand = new Vector3();
		Vector3 diff = new Vector3();
		System.out.println("bb min  : " + pathZone.min);
		// int zoneMaxDist = (int) pathZone.min.dst(pathZone.max);
		for (int i = 0; i < pointsToGenerate; i++) {
			Vector3 p = pathZone.getMin(new Vector3());
			rand.set(MathUtils.random(), MathUtils.random(), MathUtils.random());
			pathZone.getDimensions(diff).scl(rand);
			p.add(diff);

			// int dist = (int) p.dst(salg.getGoalPoint().getPoint());
			// if (dist < zoneMaxDist / 2)
			// dist *= 2 + MathUtils.random();
			// System.out.println("POINT : " + p);
			// System.out.println("Distance eurist : " + dist);

			salg.addFieldPoint(new SANode(p));
		}

		for (SANode nod : salg.getNodeField().toArray()) {
			Array<IndexedObject<SANode>> fs = new Array<>();
			for (SANode al : salg.getNodeField())
				if (al != nod)
					fs.add(new IndexedObject<SANode>(al, (int) (nod.getPoint().dst(al.getPoint()) * 100)));
			fs.sort();
			fs.reverse();
			int ra = MathUtils.random(minNe, maxNe);
			// fs.pop();
			// System.out.println(nod.getPoint());
			for (int i = 0; i < ra; i++) {
				SANode nn = fs.pop().object;
				if (!nod.getNeighbors().contains(nn, true))
					nod.addNeighbor(nn);
				if (!nn.getNeighbors().contains(nod, true))
					nn.addNeighbor(nod);
//				fs.pop();
			}
			System.out.println(nod.getNeighbors().size);

		}
		// for (SANode al : salg.getNodeField())
		// System.out.println(al + " " + al.getNeighbors().size);

		//////////////////////////////////////////////////////////////

		Array<Array<SANode>> pts = salg.getPaths(minNodes);

		for (Array<SANode> pat : pts) {
			System.out.println("Marime: " + pat.size);

			Vector3 cps[] = new Vector3[pat.size + 2];
			for (int j = 0; j < pat.size; j++)
				cps[j + 1] = pat.get(j).getPoint();
			cps[0] = cps[1];
			cps[cps.length - 1] = cps[cps.length - 2];

			paths.add(new CatmullRomSpline<Vector3>(cps, false));
		}

		// for (Array<IndexedObject<Vector3>> pat : cp) {
		//
		// Vector3 cps[] = new Vector3[pat.size + 2];
		// for (int j = 0; j < pat.size; j++)
		// cps[j + 1] = pat.get(j).object;
		// cps[0] = cps[1];
		// cps[cps.length - 1] = cps[cps.length - 2];
		//
		// paths.add(new CatmullRomSpline<Vector3>(cps, false));
		// }

		// Model tmpModel = new ModelBuilder().createCone( 5, 5, 5, 20, new
		// Material( ColorAttribute.createDiffuse( Color.GRAY ) ),
		// Usage.Position |Usage.Normal );
		// toDispose.add( tmpModel );
		// ModelInstance baza = new ModelInstance( tmpModel, overview );
		// baza.transform.setToTranslation( overview );
		// baza.calculateTransforms();
		towers[0] = new Tower(TowerType.VIEWPOINT, WorldWrapper.instance, overview, 0);
	}

	private void readData(FileHandle location) {

		Element map = null;
		try {
			map = new XmlReader().parse(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		waves = new Waves(WorldWrapper.instance);

		setLife(map.getInt("viata", 100));

		setMoney(map.getInt("money", 1001));

		Array<Element> tmpWaves = map.getChildrenByName("wave");
		tmpWaves.shrink();
		Array<IndexedObject<Element>> sortedWaves = new Array<IndexedObject<Element>>(tmpWaves.size);
		for (int i = 0; i < tmpWaves.size; i++)
			sortedWaves.add(new IndexedObject<Element>(tmpWaves.get(i), tmpWaves.get(i).getInt("index") - 1));
		sortedWaves.sort();
		tmpWaves.clear();

		for (IndexedObject<Element> wav : sortedWaves) {
			waves.addWave(wav.object.getFloat("delay", 5), wav.object.getFloat("interval", 0.5f));
			Array<Element> tmpPaths = wav.object.getChildrenByName("path");

			for (Element pat : tmpPaths) {

				int numar = pat.getInt("nr");
				for (int i = 0; i < pat.getChildCount(); i++) {

					Element monstru = pat.getChild(i);
					waves.populate(numar - 1, EnemyType.valueOf(monstru.getName().toUpperCase()),
							monstru.getInt("amount", 1));

				}
			}
		}
		sortedWaves.clear();
	}

	public CatmullRomSpline<Vector3> getClosestStartPath(Vector3 location) {
		CatmullRomSpline<Vector3> closest = paths.first();
		float dist = Float.MAX_VALUE;

		for (CatmullRomSpline<Vector3> path : getPaths()) {
			float dst2 = location.dst2(path.controlPoints[0]);
			if (dst2 < dist) {
				dist = dst2;
				closest = path;
			}
		}
		return closest;
	}

	public Vector3 getOnPath(Vector3 point, Vector3 out, float chkDst) {
		float tmpDist, t, minDist = Float.MAX_VALUE;
		final float step = 1 / chkDst;

		for (CatmullRomSpline<Vector3> path : paths) {
			t = 0;
			while (t <= 1) {
				path.valueAt(GR.temp1, t);
				tmpDist = GR.temp1.dst(point);

				if (tmpDist <= minDist) {
					minDist = tmpDist;
					if (out != null)
						out.set(GR.temp1);
				}
				t += step;
			}
		}
		return out;
	}

	public Enemy addFoe(EnemyType type, Vector3 poz) {
		Enemy inamicTemp = inamicPool.obtain().create(type, poz);
		getEnemy().add(inamicTemp);
		return inamicTemp;
	}

	public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path) {
		Enemy inamicTemp = inamicPool.obtain().create(path, type);
		getEnemy().add(inamicTemp);
		return inamicTemp;
	}

	public Ally addAlly(Vector3 duty, AllyType type) {
		Ally aliatTemp = aliatPool.obtain().create(duty, type);
		getAlly().add(aliatTemp);

		for (FightZone fz : getFightZones()) {
			if (fz.box.intersects(aliatTemp.box)) {
				fz.addAlly(aliatTemp);
				fz.aproximatePoz();
				aliatTemp.zone = fz;
				return aliatTemp;
			}
		}

		FightZone zone = addFightZone(duty);
		zone.addAlly(aliatTemp);
		aliatTemp.zone = zone;
		return aliatTemp;
	}

	public Shot addShot(ShotType type, Ray ray, float charge) {
		Shot shotTemp = shotPool.obtain().create(type, ray, charge);
		getShot().add(shotTemp);
		return shotTemp;
	}

	public FightZone addFightZone(Vector3 poz) {
		FightZone fightZone = fzPool.obtain().create(poz);
		getFightZones().add(fightZone);
		return fightZone;
	}

	private Pool<Enemy> inamicPool = new Pool<Enemy>() {

		@Override
		protected Enemy newObject() {
			return new Enemy();
		}
	};

	private Pool<Ally> aliatPool = new Pool<Ally>() {

		@Override
		protected Ally newObject() {
			return new Ally();
		}
	};

	private Pool<Shot> shotPool = new Pool<Shot>(100, 500) {

		@Override
		protected Shot newObject() {
			return new Shot();
		}
	};

	private Pool<FightZone> fzPool = new Pool<FightZone>() {

		@Override
		protected FightZone newObject() {
			return new FightZone(WorldWrapper.instance);
		}
	};

	private SearchAlg salg;

	public Pool<Enemy> getEnemyPool() {
		return inamicPool;
	}

	public Pool<Ally> getAliatPool() {
		return aliatPool;
	}

	public Pool<Shot> getShotPool() {
		return shotPool;
	}

	public Pool<FightZone> getFzPool() {
		return fzPool;
	}

	public Tower getTowerHitByRay(Ray ray) {
		for (Tower tower : towers)
			if (tower.intersectsRay(ray)) {
				return tower;
			}
		return null;
	}

	public Environment getEnvironment1() {
		return environment;
	}

	public Array<FightZone> getFightZones() {
		return fightZones;
	}

	public Array<CatmullRomSpline<Vector3>> getPaths() {
		return paths;
	}

	public Array<Ally> getAlly() {
		return ally;
	}

	public Array<BoundingBox> getColide() {
		return colide;
	}

	public BoundingBox addToColide(BoundingBox box) {
		colide.add(box);
		return box;
	}

	public void removeColiders(Array<BoundingBox> box) {
		colide.removeAll(box, false);
	}

	public Array<Enemy> getEnemy() {
		return enemy;
	}

	public void addLife(int amount) {
		setLife(getLife() + amount);
	}

	public void setLife(int viata) {
		this.life = viata;
		GameStageMaker.hudViataTurn.setText("Life " + viata);

		// TODO add event when life <= 0

	}

	public int getLife() {
		return life;
	}

	public Array<Shot> getShot() {
		return shot;
	}

	public boolean canWaveStart() {
		return canWaveStart;
	}

	public void setCanWaveStart(boolean canWaveStart) {
		this.canWaveStart = canWaveStart;
	}

	public boolean upgradeTower(Tower tower, TowerType upgrade) {
		return tower.upgradeTower(upgrade);
	}

	public boolean changeWeapon(Tower tower, WeaponType newWeapon) {
		if (TowerType.isValidForWeapon(tower.type))
			if (tower.changeWeapon(newWeapon)) {
				// System.out.println( "World changed weapon" );
				return true;
			}
		return false;
	}

	public boolean canChangeTowers(byte current, byte next, String name) {
		if (towers[next].ocupier == null || next == 0) {
			towers[current].ocupier = null;
			towers[next].ocupier = name;
			return true;
		}
		return false;
	}

	public String getMapPath() {
		return mapName;
	}

	public void setMapPath(String mapPath) {
		this.mapName = mapPath;
	}

	public Tower[] getTowers() {
		return towers;
	}

	public Vector3 getOverview() {
		return overview;
	}

	public void addMoney(int money) {
		this.money += money;
		GameStageMaker.tuMoneyMeter.setText("Money " + this.money);
	}

	public void setMoney(int money) {
		this.money = money;
		GameStageMaker.tuMoneyMeter.setText("Money " + this.money);
	}

	public int getMoney() {
		return money;
	}

	public boolean fireFromTower(Tower tower) {
		return tower.fireWeapon();
	}

	public boolean setTowerFireHold(Tower tower, boolean hold) {
		if (!tower.hasWeapon())
			return false;
		if (!tower.isWeaponType(FireType.FIREHOLD))
			hold = false;

		tower.isFiringHold = hold;

		return true;
	}

	public void reset() {
		if (mediu.size > 0)
			mediu.first().model.dispose();
		mediu.clear();
		enemy.clear();
		ally.clear();
		shot.clear();
		colide.clear();
		fightZones.clear();
		if (paths != null)
			paths.clear();

		overview.set(10, 10, 10);
		canWaveStart = false;
	}

	@Override
	public void dispose() {

		reset();

		if (towers != null)
			for (Tower dis : towers)
				dis.dispose();

		for (Disposable dis : toDispose)
			dis.dispose();
	}
}
