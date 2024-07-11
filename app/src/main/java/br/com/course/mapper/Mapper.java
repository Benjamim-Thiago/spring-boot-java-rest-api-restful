package br.com.course.mapper;

import java.util.ArrayList;
import java.util.List;


import com.github.dozermapper.core.DozerBeanMapperBuilder;

public class Mapper {
	
	private static com.github.dozermapper.core.Mapper mapperBuilder = DozerBeanMapperBuilder.buildDefault();

	public static <O, D> D parseObject(O origin, Class<D> destination) {
		return mapperBuilder.map(origin, destination);
	}
	
	public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination) {
		List<D> destinationObjects = new ArrayList<D>();
		for (O o : origin) {
			destinationObjects.add(mapperBuilder.map(o, destination));
		}
		return destinationObjects;
	}

}
