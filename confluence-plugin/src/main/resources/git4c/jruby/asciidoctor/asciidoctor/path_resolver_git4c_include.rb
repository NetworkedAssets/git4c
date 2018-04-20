# encoding: UTF-8
module Asciidoctor

  class Pair < Struct.new(:one, :two)
    def to_s
      "(#{self.one}, #{self.two})"
    end
  end

  class ArrayHolder

    @@file_array = []

    def self.add_to_array(element)
      @@file_array << element
    end

    def get_array
      @@file_array
    end

    def clear_array
      @@file_array.clear
    end

  end

  module Git4cIncludeInterceptor

    def system_path(target, start = nil, jail = nil, opts = {})

      if target.end_with? ".adoc"
        ArrayHolder.add_to_array(Pair.new(start, target))
      end

      super(target, start, jail, opts)
    end

  end

  class PathResolver
    prepend Git4cIncludeInterceptor
  end

end
